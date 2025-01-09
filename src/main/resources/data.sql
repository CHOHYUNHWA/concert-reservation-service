INSERT INTO user (name) VALUES ('test');

INSERT INTO point (user_id, amount) VALUES ('1', 0);

INSERT INTO concert (title, description, status) VALUES
                                                     ('Concert 1', 'Description of Concert 1', 'OPEN'),
                                                     ('Concert 2', 'Description of Concert 2', 'CLOSED');

INSERT INTO concert_schedule (concert_id, available_reservation_time, concert_time)
VALUES (1, '2025-01-08 12:00:00', '2025-01-15 18:00:00');


-- 예약 가능한 시간이 아직 오지 않은 경우
INSERT INTO concert_schedule (concert_id, available_reservation_time, concert_time)
VALUES (1, '2025-01-013 12:00:00', '2025-01-15 18:00:00');

-- 예약 마감 시간이 지난 경우
INSERT INTO concert_schedule (concert_id, available_reservation_time, concert_time)
VALUES (1, '2025-01-8 12:00:00', '2025-01-9 18:00:00');

-- 예약 가능한 좌석 (1~25)
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 1, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 2, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 3, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 4, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 5, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 6, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 7, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 8, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 9, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 10, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 11, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 12, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 13, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 14, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 15, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 16, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 17, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 18, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 19, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 20, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 21, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 22, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 23, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 24, 'AVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 25, 'AVAILABLE', 10000);

-- 예약 불가능한 좌석 (26~50)
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 26, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 27, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 28, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 29, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 30, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 31, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 32, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 33, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 34, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 35, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 36, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 37, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 38, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 39, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 40, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 41, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 42, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 43, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 44, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 45, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 46, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 47, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 48, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 49, 'UNAVAILABLE', 10000);
INSERT INTO seat (concert_schedule_id, seat_number, status, seat_price) VALUES (1, 50, 'UNAVAILABLE', 10000);