package com.backthree.cohobby.domain.chatting.service;

import com.backthree.cohobby.domain.chatting.dto.ChattingDto;
import com.backthree.cohobby.domain.chatting.dto.ChattingRoomDto;
import com.backthree.cohobby.domain.chatting.entity.Chatting;
import com.backthree.cohobby.domain.chatting.entity.ChattingRoom;
import com.backthree.cohobby.domain.chatting.repository.ChattingRepository;
import com.backthree.cohobby.domain.chatting.repository.ChattingRoomRepository;
import com.backthree.cohobby.domain.post.repository.PostRepository;
import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import com.backthree.cohobby.domain.rent.repository.RentRepository;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.rent.entity.RentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChattingService {
    private final ChattingRepository chattingRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RentRepository rentRepository;

    public List<ChattingDto> getChattingByRoomId(Long roomId) {
        return chattingRepository.findByRoomId(roomId).stream()
                .map(chat -> ChattingDto.builder()
                        .id(chat.getId())
                        .roomId(chat.getRoom().getId())
                        .senderId(chat.getSender().getId())
                        .receiverId(chat.getReceiver().getId())
                        .text(chat.getText())
                        .time(chat.getTime())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChattingRoomDto> getRoomsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return chattingRoomRepository.findByOwnerOrBorrower(user, user).stream()
                .map(room -> {
                    // 상대방 정보 찾기
                    User peer = room.getOwner().getId().equals(userId) 
                            ? room.getBorrower() 
                            : room.getOwner();
                    
                    // 마지막 메시지 조회
                    Chatting lastChatting = chattingRepository.findLatestByRoomId(room.getId())
                            .orElse(null);
                    
                    // 읽지 않은 메시지 수 계산
                    Long lastReadMessageId = room.getLastReadOf(user);
                    int unreadCount = 0;
                    if (lastReadMessageId != null && lastChatting != null) {
                        unreadCount = (int) chattingRepository.countByRoom_IdAndIdGreaterThanAndSender_IdNot(
                                room.getId(), lastReadMessageId, userId);
                    } else if (lastChatting != null && !lastChatting.getSender().getId().equals(userId)) {
                        // 읽은 메시지가 없고, 마지막 메시지가 상대방이 보낸 것이면 1개 이상
                        unreadCount = 1;
                    }
                    
                    return ChattingRoomDto.builder()
                            .id(room.getId())
                            .postId(room.getPost().getId())
                            .ownerId(room.getOwner().getId())
                            .borrowerId(room.getBorrower().getId())
                            .name(room.getName())
                            .lastMessage(lastChatting != null ? lastChatting.getText() : null)
                            .lastMessageTime(lastChatting != null ? lastChatting.getTime() : null)
                            .peerName(peer.getNickname())
                            .peerId(peer.getId())
                            .peerProfilePicture(peer.getProfilePicture())
                            .postGoods(room.getPost().getGoods())
                            .unreadCount(unreadCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ChattingRoomDto createRoom(Long postId, Long borrowerId, String startDate, String endDate, Integer totalPrice) {

        User borrower = userRepository.findById(borrowerId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();
        //post에 있는 user 가져오기
        User owner = post.getUser();
        Long ownerId = owner.getId();
        // 필요 시 중복 구분용 suffix 예: + " #" + System.currentTimeMillis()
        String roomName = owner.getNickname() + " & " + borrower.getNickname();

        // Rent를 먼저 생성 (ChattingRoom의 rent_id가 NOT NULL이므로)
        Rent.RentBuilder rentBuilder = Rent.builder()
                .post(post)
                .owner(owner)
                .borrower(borrower)
                .status(RentStatus.CREATED);

        // 날짜 정보가 있으면 설정
        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);
            rentBuilder.startAt(start.atStartOfDay().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime());
            rentBuilder.duedate(end.atStartOfDay().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime());
        }

        // 가격 정보가 있으면 설정
        if (totalPrice != null) {
            rentBuilder.totalPrice(totalPrice);
            rentBuilder.currency("KRW"); // 기본 통화는 KRW
        }

        Rent rent = rentRepository.save(rentBuilder.build());

        // Rent를 포함한 ChattingRoom 생성 및 저장
        ChattingRoom room = chattingRoomRepository.save(ChattingRoom.builder()
                .post(post)
                .owner(owner)
                .borrower(borrower)
                .name(roomName)
                .rent(rent)  // Rent를 포함해서 저장
                .build());

        return ChattingRoomDto.builder()
                .id(room.getId())
                .postId(room.getPost().getId())
                .ownerId(room.getOwner().getId())
                .borrowerId(room.getBorrower().getId())
                .name(room.getName())
                .build();
    }
}
