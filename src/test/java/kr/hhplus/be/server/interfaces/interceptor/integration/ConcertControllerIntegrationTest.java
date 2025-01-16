package kr.hhplus.be.server.interfaces.interceptor.integration;

import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.infra.repository.impl.ReservationRepositoryImpl;
import kr.hhplus.be.server.infra.repository.jpa.*;
import kr.hhplus.be.server.support.type.ConcertStatus;
import kr.hhplus.be.server.support.type.QueueStatus;
import kr.hhplus.be.server.support.type.ReservationStatus;
import kr.hhplus.be.server.support.type.SeatStatus;
import kr.hhplus.be.server.util.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ConcertControllerIntegrationTest {

    private Queue queue;
    private Queue expiredQueue;
    private Concert concert;
    private ConcertSchedule concertSchedule;


    @Autowired
    private MockMvc mvc;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private ConcertJpaRepository concertJpaRepository;
    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;
    @Autowired
    private SeatJpaRepository seatJpaRepository;
    @Autowired
    private ReservationRepositoryImpl reservationRepository;
    @Autowired
    private QueueJpaRepository queueJpaRepository;
    @Autowired
    private QueueService queueService;

    @BeforeEach
    void setUp() {
        databaseCleanUp.execute();

        User user = User.builder()
                .name("유저")
                .build();

        userJpaRepository.save(user);

        queue = Queue.builder()
                .token("valid-token")
                .createdAt(LocalDateTime.now())
                .enteredAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .status(QueueStatus.ACTIVE)
                .build();

        queueJpaRepository.save(queue);

        expiredQueue = Queue
                .builder()
                .token("token")
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .enteredAt(LocalDateTime.now().minusMinutes(10))
                .expiredAt(LocalDateTime.now().minusMinutes(5))
                .status(QueueStatus.EXPIRED)
                .build();

        queueJpaRepository.save(expiredQueue);

        Point point = Point.builder()
                .userId(user.getId())
                .amount(0L)
                .build();

        pointJpaRepository.save(point);

        concert = Concert.builder()
                .title("콘서트")
                .description("콘서트내용")
                .status(ConcertStatus.OPEN)
                .build();

        concertJpaRepository.save(concert);

        concertSchedule = ConcertSchedule.builder()
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusDays(5))
                .concertTime(LocalDateTime.now().plusDays(30))
                .build();

        concertScheduleJpaRepository.save(concertSchedule);

        Seat seat = Seat.builder()
                .seatNumber(1L)
                .seatPrice(100L)
                .seatStatus(SeatStatus.AVAILABLE)
                .reservedAt(null)
                .concertScheduleId(concertSchedule.getId())
                .build();

        seatJpaRepository.save(seat);

        Reservation reservation = Reservation.builder()
                .concertScheduleId(concertSchedule.getId())
                .userId(user.getId())
                .concertId(concert.getId())
                .seatId(seat.getId())
                .status(ReservationStatus.PAYMENT_WAITING)
                .reservedAt(LocalDateTime.now())
                .build();

        reservationRepository.save(reservation);
    }

    @Test
    void 정상_토큰인_경우_콘서트_조회_성공() throws Exception {
        //given

        //when//then
        mvc.perform(get("/api/concerts")
                        .header("Token", queue.getToken()))
                .andExpect(status().isOk());
    }

    @Test
    void 헤더에_토큰이_없는_경우_콘서트_조회_실패() throws Exception {
        //given

        //when //then
        mvc.perform(get("/api/concerts"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("없는 토큰 입니다."));
    }

    @Test
    void 헤더에_토큰이_만료된_경우_콘서트_조회_실패() throws Exception {
        //given

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredTime = expiredQueue.getExpiredAt();

        //when //then
        mvc.perform(get("/api/concerts")
                        .header("Token", expiredQueue.getToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않는 토큰 입니다."));
    }

    @Test
    void 정상_토큰인_경우_콘서트스케쥴_조회_성공() throws Exception {
        //given

        //when//then
        mvc.perform(get("/api/concerts/{concertId}/schedules", concert.getId())
                        .header("Token", queue.getToken()))
                .andExpect(status().isOk());
    }

    @Test
    void 헤더에_토큰이_없는_경우_콘서트스케쥴_조회_실패() throws Exception {
        //given

        //when //then
        mvc.perform(get("/api/concerts/{concertId}/schedules", concert.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("없는 토큰 입니다."));
    }

    @Test
    void 헤더에_토큰이_만료된_경우_콘서트스케쥴_조회_실패() throws Exception {
        //given

        //when //then
        mvc.perform(get("/api/concerts/{concertId}/schedules", concert.getId())
                        .header("Token", expiredQueue.getToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않는 토큰 입니다."));
    }

    @Test
    void 정상_토큰인_경우_좌석들_조회_성공() throws Exception {
        //given

        //when//then
        mvc.perform(get("/api/concerts/{concertId}/schedules/{concertScheduleId}/seats", concert.getId(), concertSchedule.getId())
                        .header("Token", queue.getToken()))
                .andExpect(status().isOk());
    }

    @Test
    void 헤더에_토큰이_없는_경우_좌석들_조회_실패() throws Exception {
        //given

        //when //then
        mvc.perform(get("/api/concerts/{concertId}/schedules/{concertScheduleId}/seats", concert.getId(), concertSchedule.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("없는 토큰 입니다."));
    }

    @Test
    void 헤더에_토큰이_만료된_경우_좌석들_조회_실패() throws Exception {
        //given

        //when //then
        mvc.perform(get("/api/concerts/{concertId}/schedules/{concertScheduleId}/seats", concert.getId(), concertSchedule.getId())
                        .header("Token", expiredQueue.getToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않는 토큰 입니다."));
    }
}
