-- User id 4 관련 Mock Data
-- User 4가 owner인 Post들 (3개)
INSERT INTO post (goods, defect_status, caution, daily_price, deposit, image_url, status, available_from, available_until, purchased_at, hobby_id, user_id, created_at, updated_at)
VALUES
('골프채 세트', '사용감 있지만 전반적으로 양호한 상태입니다', '사용 후 깨끗이 세척해주세요', 15000, 50000, 'https://example.com/golf.jpg', 'PUBLISHED', '2025-01-01', '2025-12-31', '2023-06-15', 1, 4, NOW(), NOW());

SET @post_id_1 = LAST_INSERT_ID();

INSERT INTO post (goods, defect_status, caution, daily_price, deposit, image_url, status, available_from, available_until, purchased_at, hobby_id, user_id, created_at, updated_at)
VALUES
('테니스 라켓', '거의 새것 같은 상태', '라켓 케이스와 함께 제공됩니다', 10000, 30000, 'https://example.com/tennis.jpg', 'PUBLISHED', '2025-01-01', '2025-12-31', '2024-03-20', 1, 4, NOW(), NOW());

SET @post_id_2 = LAST_INSERT_ID();

INSERT INTO post (goods, defect_status, caution, daily_price, deposit, image_url, status, available_from, available_until, purchased_at, hobby_id, user_id, created_at, updated_at)
VALUES
('배드민턴 라켓', '약간의 사용감 있음', '셔틀콕 2개 포함', 8000, 20000, 'https://example.com/badminton.jpg', 'PUBLISHED', '2025-01-01', '2025-12-31', '2023-11-10', 1, 4, NOW(), NOW());

SET @post_id_3 = LAST_INSERT_ID();

-- User 4가 borrower인 Post들 (다른 사용자들의 Post에 대여 요청)
-- 먼저 다른 사용자들의 Post가 필요하므로 생성
INSERT INTO post (goods, defect_status, caution, daily_price, deposit, image_url, status, available_from, available_until, purchased_at, hobby_id, user_id, created_at, updated_at)
VALUES
('축구공', '새것', '공기압 확인 후 사용해주세요', 5000, 10000, 'https://example.com/soccer.jpg', 'PUBLISHED', '2025-01-01', '2025-12-31', '2024-05-01', 1, 1, NOW(), NOW());

SET @post_id_4 = LAST_INSERT_ID();

INSERT INTO post (goods, defect_status, caution, daily_price, deposit, image_url, status, available_from, available_until, purchased_at, hobby_id, user_id, created_at, updated_at)
VALUES
('야구 글러브', '사용감 있음', '오른손잡이용', 7000, 25000, 'https://example.com/baseball.jpg', 'PUBLISHED', '2025-01-01', '2025-12-31', '2023-08-15', 1, 2, NOW(), NOW());

SET @post_id_5 = LAST_INSERT_ID();

-- User 4가 owner인 Post들에 대한 Rent와 ChattingRoom 생성
-- Post 1 (골프채 세트) - User 1이 borrower
INSERT INTO rent (start_at, duedate, rule, status, total_price, currency, cancel_policy, owner_id, borrower_id, post_id, created_at, updated_at)
VALUES
('2025-01-15 10:00:00', '2025-01-17 18:00:00', '사용 후 깨끗이 세척해주세요. 손상 시 보증금에서 차감됩니다.', 'CONFIRMED', 30000, 'KRW', '{"cancelBefore24h": "100%", "cancelAfter24h": "50%"}', 4, 1, @post_id_1, NOW(), NOW());

SET @rent_id_1 = LAST_INSERT_ID();

INSERT INTO chatting_room (name, post_id, owner_id, borrower_id, rent_id, owner_last_read_message_id, borrower_last_read_message_id)
VALUES
('골프채 세트 대여', @post_id_1, 4, 1, @rent_id_1, NULL, NULL);

SET @room_id_1 = LAST_INSERT_ID();

UPDATE rent SET chatting_room_id = @room_id_1 WHERE id = @rent_id_1;

-- Post 2 (테니스 라켓) - User 2가 borrower
INSERT INTO rent (start_at, duedate, rule, status, total_price, currency, cancel_policy, owner_id, borrower_id, post_id, created_at, updated_at)
VALUES
('2025-01-20 14:00:00', '2025-01-22 20:00:00', '라켓 케이스에 보관해주세요.', 'ONGOING', 20000, 'KRW', '{"cancelBefore24h": "100%", "cancelAfter24h": "50%"}', 4, 2, @post_id_2, NOW(), NOW());

SET @rent_id_2 = LAST_INSERT_ID();

INSERT INTO chatting_room (name, post_id, owner_id, borrower_id, rent_id, owner_last_read_message_id, borrower_last_read_message_id)
VALUES
('테니스 라켓 대여', @post_id_2, 4, 2, @rent_id_2, NULL, NULL);

SET @room_id_2 = LAST_INSERT_ID();

UPDATE rent SET chatting_room_id = @room_id_2 WHERE id = @rent_id_2;

-- Post 3 (배드민턴 라켓) - User 3이 borrower
INSERT INTO rent (start_at, duedate, rule, status, total_price, currency, cancel_policy, owner_id, borrower_id, post_id, created_at, updated_at)
VALUES
('2025-01-25 09:00:00', '2025-01-27 17:00:00', '셔틀콕은 반납 시 함께 반납해주세요.', 'CREATED', 16000, 'KRW', '{"cancelBefore24h": "100%", "cancelAfter24h": "50%"}', 4, 3, @post_id_3, NOW(), NOW());

SET @rent_id_3 = LAST_INSERT_ID();

INSERT INTO chatting_room (name, post_id, owner_id, borrower_id, rent_id, owner_last_read_message_id, borrower_last_read_message_id)
VALUES
('배드민턴 라켓 대여', @post_id_3, 4, 3, @rent_id_3, NULL, NULL);

SET @room_id_3 = LAST_INSERT_ID();

UPDATE rent SET chatting_room_id = @room_id_3 WHERE id = @rent_id_3;

-- User 4가 borrower인 Rent와 ChattingRoom 생성
-- Post 4 (축구공) - User 4가 borrower, User 1이 owner
INSERT INTO rent (start_at, duedate, rule, status, total_price, currency, cancel_policy, owner_id, borrower_id, post_id, created_at, updated_at)
VALUES
('2025-01-18 11:00:00', '2025-01-20 19:00:00', '공기압 확인 후 사용해주세요.', 'COMPLETED', 10000, 'KRW', '{"cancelBefore24h": "100%", "cancelAfter24h": "50%"}', 1, 4, @post_id_4, NOW(), NOW());

SET @rent_id_4 = LAST_INSERT_ID();

INSERT INTO chatting_room (name, post_id, owner_id, borrower_id, rent_id, owner_last_read_message_id, borrower_last_read_message_id)
VALUES
('축구공 대여', @post_id_4, 1, 4, @rent_id_4, NULL, NULL);

SET @room_id_4 = LAST_INSERT_ID();

UPDATE rent SET chatting_room_id = @room_id_4 WHERE id = @rent_id_4;

-- Post 5 (야구 글러브) - User 4가 borrower, User 2가 owner
INSERT INTO rent (start_at, duedate, rule, status, total_price, currency, cancel_policy, owner_id, borrower_id, post_id, created_at, updated_at)
VALUES
('2025-01-22 13:00:00', '2025-01-24 21:00:00', '오른손잡이용입니다. 사용 후 깨끗이 세척해주세요.', 'ONGOING', 14000, 'KRW', '{"cancelBefore24h": "100%", "cancelAfter24h": "50%"}', 2, 4, @post_id_5, NOW(), NOW());

SET @rent_id_5 = LAST_INSERT_ID();

INSERT INTO chatting_room (name, post_id, owner_id, borrower_id, rent_id, owner_last_read_message_id, borrower_last_read_message_id)
VALUES
('야구 글러브 대여', @post_id_5, 2, 4, @rent_id_5, NULL, NULL);

SET @room_id_5 = LAST_INSERT_ID();

UPDATE rent SET chatting_room_id = @room_id_5 WHERE id = @rent_id_5;

-- Chatting 메시지들 추가
-- Room 1 (골프채 세트) - User 1과 User 4의 대화
INSERT INTO chatting (room_id, text, time, sender_id, receiver_id, created_at, updated_at)
VALUES
(@room_id_1, '안녕하세요! 골프채 세트 대여 가능한가요?', '2025-01-10 14:30:00', 1, 4, NOW(), NOW()),
(@room_id_1, '네, 가능합니다! 언제 필요하신가요?', '2025-01-10 14:32:00', 4, 1, NOW(), NOW()),
(@room_id_1, '이번 주말에 사용하려고 하는데, 금요일 저녁에 픽업 가능할까요?', '2025-01-10 14:33:00', 1, 4, NOW(), NOW()),
(@room_id_1, '금요일 6시 이후면 가능해요! 위치는 강남역 근처입니다.', '2025-01-10 14:35:00', 4, 1, NOW(), NOW()),
(@room_id_1, '완벽해요! 대여 기간은 2박 3일 정도로 생각하고 있어요.', '2025-01-10 14:37:00', 1, 4, NOW(), NOW()),
(@room_id_1, '2박 3일이면 15,000원입니다. 보증금은 50,000원이구요.', '2025-01-10 14:40:00', 4, 1, NOW(), NOW()),
(@room_id_1, '좋아요! 그럼 내일 오후에 만나서 거래할 수 있을까요?', '2025-01-10 14:42:00', 1, 4, NOW(), NOW()),
(@room_id_1, '네 좋습니다! 강남역 2번 출구에서 만나요.', '2025-01-10 14:45:00', 4, 1, NOW(), NOW());

-- Room 2 (테니스 라켓) - User 2와 User 4의 대화
INSERT INTO chatting (room_id, text, time, sender_id, receiver_id, created_at, updated_at)
VALUES
(@room_id_2, '테니스 라켓 대여 문의드립니다!', '2025-01-15 10:20:00', 2, 4, NOW(), NOW()),
(@room_id_2, '네, 언제 필요하신가요?', '2025-01-15 10:22:00', 4, 2, NOW(), NOW()),
(@room_id_2, '다음 주 월요일부터 수요일까지요.', '2025-01-15 10:25:00', 2, 4, NOW(), NOW()),
(@room_id_2, '가능합니다! 픽업은 일요일 저녁에 가능하신가요?', '2025-01-15 10:27:00', 4, 2, NOW(), NOW()),
(@room_id_2, '네, 가능합니다!', '2025-01-15 10:30:00', 2, 4, NOW(), NOW());

-- Room 3 (배드민턴 라켓) - User 3과 User 4의 대화
INSERT INTO chatting (room_id, text, time, sender_id, receiver_id, created_at, updated_at)
VALUES
(@room_id_3, '배드민턴 라켓 대여 가능한가요?', '2025-01-20 09:15:00', 3, 4, NOW(), NOW()),
(@room_id_3, '네, 가능합니다!', '2025-01-20 09:17:00', 4, 3, NOW(), NOW()),
(@room_id_3, '셔틀콕도 포함되어 있나요?', '2025-01-20 09:18:00', 3, 4, NOW(), NOW()),
(@room_id_3, '네, 셔틀콕 2개 포함되어 있습니다!', '2025-01-20 09:20:00', 4, 3, NOW(), NOW());

-- Room 4 (축구공) - User 1과 User 4의 대화 (User 4가 borrower)
INSERT INTO chatting (room_id, text, time, sender_id, receiver_id, created_at, updated_at)
VALUES
(@room_id_4, '축구공 대여 가능한가요?', '2025-01-12 16:00:00', 4, 1, NOW(), NOW()),
(@room_id_4, '네, 가능합니다!', '2025-01-12 16:02:00', 1, 4, NOW(), NOW()),
(@room_id_4, '이번 주말에 사용하려고 해요.', '2025-01-12 16:05:00', 4, 1, NOW(), NOW()),
(@room_id_4, '좋아요! 토요일 오전에 픽업 가능하신가요?', '2025-01-12 16:07:00', 1, 4, NOW(), NOW()),
(@room_id_4, '네, 가능합니다!', '2025-01-12 16:10:00', 4, 1, NOW(), NOW()),
(@room_id_4, '반납 완료했습니다! 감사합니다.', '2025-01-20 20:00:00', 4, 1, NOW(), NOW()),
(@room_id_4, '네, 감사합니다!', '2025-01-20 20:02:00', 1, 4, NOW(), NOW());

-- Room 5 (야구 글러브) - User 2와 User 4의 대화 (User 4가 borrower)
INSERT INTO chatting (room_id, text, time, sender_id, receiver_id, created_at, updated_at)
VALUES
(@room_id_5, '야구 글러브 대여 문의드립니다!', '2025-01-18 11:30:00', 4, 2, NOW(), NOW()),
(@room_id_5, '네, 가능합니다! 오른손잡이용인데 괜찮으신가요?', '2025-01-18 11:32:00', 2, 4, NOW(), NOW()),
(@room_id_5, '네, 괜찮습니다!', '2025-01-18 11:35:00', 4, 2, NOW(), NOW()),
(@room_id_5, '다음 주 화요일부터 목요일까지 사용 가능한가요?', '2025-01-18 11:37:00', 4, 2, NOW(), NOW()),
(@room_id_5, '네, 가능합니다!', '2025-01-18 11:40:00', 2, 4, NOW(), NOW());

-- User 4가 좋아요를 누른 Post들 (다른 사용자들의 Post)
INSERT INTO likes (post_id, user_id)
VALUES
(@post_id_4, 4),
(@post_id_5, 4);

-- User 4의 Post에 좋아요를 누른 다른 사용자들
INSERT INTO likes (post_id, user_id)
VALUES
(@post_id_1, 1),
(@post_id_1, 2),
(@post_id_2, 1),
(@post_id_2, 3),
(@post_id_3, 2);

