package com.backthree.cohobby.domain.post.service;


import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.post.entity.PostStatus;
import com.backthree.cohobby.domain.post.entity.QPost;
import com.backthree.cohobby.domain.post.repository.PostRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostQueryService {
    private final PostRepository postRepository;
    
    public List<Post> getPosts(String query, String type, Long excludeUserId) {
        QPost post = QPost.post;
        BooleanBuilder builder = new BooleanBuilder();

        // PUBLISHED 상태인 게시물만 조회
        builder.and(post.status.eq(PostStatus.PUBLISHED));

        // 자신의 게시물 제외 (로그인한 경우에만)
        if (excludeUserId != null) {
            builder.and(post.user.id.ne(excludeUserId));
        }

        // type과 query가 null이 아닐 때만 필터링 조건 추가
        if (type != null && query != null && !query.trim().isEmpty()) {
            if (type.equals("category")) {
                // query를 Long으로 변환하여 카테고리 ID로 조회
                try {
                    Long categoryId = Long.parseLong(query);
                    builder.and(post.hobby.category.id.eq(categoryId));
                } catch (NumberFormatException e) {
                    // 숫자가 아닌 경우 카테고리 이름으로 조회
                    builder.and(post.hobby.category.name.eq(query));
                }
            } else if (type.equals("hobby")) {
                // query를 Long으로 변환하여 취미 ID로 조회
                try {
                    Long hobbyId = Long.parseLong(query);
                    builder.and(post.hobby.id.eq(hobbyId));
                } catch (NumberFormatException e) {
                    // 숫자가 아닌 경우 취미 이름으로 조회
                    builder.and(post.hobby.name.eq(query));
                }
            } else if (type.equals("search")) {
                // 검색 타입인 경우 상품명(goods)만 검색
                builder.and(post.goods.contains(query));
            }
        } else if (type != null && type.equals("search")) {
            // 검색 타입인데 query가 null이거나 빈 문자열이면 빈 결과 반환
            builder.and(post.id.eq(-1L)); // 항상 false가 되는 조건
        }
        
        List<Post> postList = postRepository.getPosts(builder);
        return postList;
    }
}
