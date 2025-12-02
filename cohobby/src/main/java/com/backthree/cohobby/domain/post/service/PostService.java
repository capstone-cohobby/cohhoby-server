package com.backthree.cohobby.domain.post.service;

import com.backthree.cohobby.domain.hobby.entity.Hobby;
import com.backthree.cohobby.domain.post.dto.request.*;
import com.backthree.cohobby.domain.post.dto.response.*;
import com.backthree.cohobby.domain.post.entity.Image;
import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.post.entity.PostStatus;
import com.backthree.cohobby.domain.post.repository.ImageRepository;
import com.backthree.cohobby.domain.post.repository.PostRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import com.backthree.cohobby.domain.user.service.UserService;
import com.backthree.cohobby.domain.hobby.service.HobbyService;
import com.backthree.cohobby.global.exception.GeneralException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final HobbyService hobbyService;
    private final S3Template s3Template;
    private final ImageRepository imageRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request, Long userId){
        Long hobbyId = request.getHobbyId();

        // ID를 기반으로 각 엔티티가 존재하는지 조회
        // 더미 유저 조회 (없으면 예외)
        if (!userService.existsById(userId)) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }
        if (!hobbyService.existsById(hobbyId)) {
            throw new GeneralException(ErrorStatus.HOBBY_NOT_FOUND);
        }

        // 검증 후, 참조(프록시)만 가져와서 외래키 관계 설정
        User userReference = userService.findUserReferenceById(userId);
        Hobby hobbyReference = hobbyService.findHobbyById(hobbyId);

        // Post 엔티티 생성
        Post newPost = Post.builder()
                .goods(request.getGoods())
                .user(userReference)
                .hobby(hobbyReference)
                .build();

        // 생성된 Post 엔티티를 Repository에 저장
        Post savedPost = postRepository.save(newPost);

        return CreatePostResponse.fromEntity(savedPost);
    }

    @Transactional
    public UpdateDetailResponse updateDetailPost(Long postId, UpdateDetailRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // 권한 및 상태 가드
        if (!Objects.equals(post.getUser().getId(), userId)) {
            throw new GeneralException(ErrorStatus.POST_AUTHOR_MISMATCH);
        }
        if (post.getStatus() != PostStatus.DRAFT) {
            throw new GeneralException(ErrorStatus.POST_STATUS_CONFLICT);
        }

        // 부분 업데이트 (null 무시)
        if (request.getPurchasedAt() != null)      post.setPurchasedAt(request.getPurchasedAt());
        if (request.getAvailableFrom() != null)    post.setAvailableFrom(request.getAvailableFrom());
        if (request.getAvailableUntil() != null)   post.setAvailableUntil(request.getAvailableUntil());
        if (request.getDefectStatus() != null)     post.setDefectStatus(request.getDefectStatus());

        // 도메인 불변식(기간/구입일 등) 검증 - 엔티티 메서드가 있다면 그걸 호출
        post.validatePeriod();

        postRepository.save(post);
        return UpdateDetailResponse.fromEntity(post);
    }
    @Transactional
    public UpdatePricingResponse updatePricingPost(Long postId, UpdatePricingRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        // 권한 및 상태 가드
        if (!Objects.equals(post.getUser().getId(), userId)) {
            throw new GeneralException(ErrorStatus.POST_AUTHOR_MISMATCH);
        }
        if (post.getStatus() != PostStatus.DRAFT) {
            throw new GeneralException(ErrorStatus.POST_STATUS_CONFLICT);
        }
        // 부분 업데이트 (null 무시)
        if (request.getDailyPrice() != null)      post.setDailyPrice(request.getDailyPrice());
        if (request.getDeposit() != null)      post.setDeposit(request.getDeposit());
        if(request.getCaution() != null)       post.setCaution(request.getCaution());
        post.setStatus(PostStatus.PUBLISHED);
        postRepository.save(post);

        return UpdatePricingResponse.fromEntity(post);
    }

    // 이미지 업로드
    // 1) 단일 파일 업로드
    public String uploadSingleImage(MultipartFile file){
        // 입력값 없으면 null 반환
        if (file == null || file.isEmpty()){
            return null;
        }

        //파일 형식 검사
        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("image/")){
            throw new GeneralException(ErrorStatus.NOT_IMAGE_FILE);
        }

        try(InputStream inputStream = file.getInputStream()){
            //파일 이름 중복 방지(UUID 사용)
            String originalFileName = file.getOriginalFilename();
            String extension = getExtension(originalFileName);
            String uuidFileName = UUID.randomUUID().toString() + extension;

            //메타데이터 설정
            ObjectMetadata metadata = ObjectMetadata.builder()
                    .contentType(contentType)
                    .build();

            // upload의 리턴값 받아서 s3에 업로드
            S3Resource resource = s3Template.upload(bucket, uuidFileName, inputStream, metadata);
            return resource.getURL().toString();
        } catch(IOException e){
            throw new GeneralException(ErrorStatus.S3_UPLOAD_FAIL);
        } catch(Exception e){
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2) 다중 파일 업로드
    @Transactional
    public UpdateImageResponse updateS3Images(Long postId, UpdateImageRequest request, Long userId) {
        //유저 및 게시글 검증
        if(!userService.existsById(userId)){
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        if (!Objects.equals(post.getUser().getId(), userId)) {
            throw new GeneralException(ErrorStatus.POST_AUTHOR_MISMATCH);
        }
        if (post.getStatus() != PostStatus.DRAFT) {
            throw new GeneralException(ErrorStatus.POST_STATUS_CONFLICT);
        }

        // 올린 이미지 하나도 없으면 빈 리스트 반환
        List<MultipartFile> files = request.getImages();
        if(files == null || files.isEmpty()){
            return UpdateImageResponse.from(Collections.emptyList());
        }

        // 이미지 파일별로 업로드 & 저장
        List<String> uploadedUrls = new ArrayList<>();
        for(MultipartFile file : files){
            String imageUrl = uploadSingleImage(file);
            if(imageUrl != null){
                uploadedUrls.add(imageUrl);
            }
            // DB에 저장
            Image postImage = Image.builder()
                    .post(post)
                    .imageUrl(imageUrl)
                    .build();
            imageRepository.save(postImage);
        }

        return UpdateImageResponse.from(uploadedUrls);
    }

    // 게시물 상세 조회
    @Transactional(readOnly = true)
    public GetPostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        
        // 이미지 로딩 (LAZY 로딩을 위해)
        post.getImages().size();
        
        // PUBLISHED 상태인 게시물만 조회 가능 (작성자는 DRAFT도 조회 가능)
        // TODO: 작성자 확인 로직 추가 필요시
        
        return GetPostDetailResponse.fromEntity(post);
    }

    // 내 등록 상품 조회
    @Transactional(readOnly = true)
    public List<GetPostResponse> getMyPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        
        // PUBLISHED 상태인 게시물만 조회
        List<Post> posts = postRepository.findByUserAndStatusOrderByCreatedAtDesc(user, PostStatus.PUBLISHED);
        
        // Image 엔티티 로딩 (LAZY 로딩을 위해)
        posts.forEach(post -> post.getImages().size());
        
        return posts.stream()
                .map(GetPostResponse::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }

    // 확장자 추출 메서드
    private String getExtension(String fileName) {
        if(fileName == null){
            throw new GeneralException(ErrorStatus.INVALID_FILE_EXTENSION);
        }

        try{
            return fileName.substring(fileName.lastIndexOf("."));
        } catch(StringIndexOutOfBoundsException e){
            throw new GeneralException(ErrorStatus.INVALID_FILE_EXTENSION);
        }
    }
}
