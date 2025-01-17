package kr.hhplus.be.server.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.infra.repository.impl.ReservationRepositoryImpl;
import kr.hhplus.be.server.infra.repository.jpa.*;
import kr.hhplus.be.server.interfaces.dto.payment.PaymentHttpDto;
import kr.hhplus.be.server.support.type.*;
import kr.hhplus.be.server.util.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerIntegrationTest {

    private Queue queue;
    private Queue expiredQueue;
    private Seat seat;
    private PaymentHttpDto.PaymentRequestDto request;

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
    private ObjectMapper objectMapper; // SpringBootTest에서 자동 주입

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
                .amount(5000L)
                .build();

        pointJpaRepository.save(point);

        Concert concert = Concert.builder()
                .title("콘서트")
                .description("콘서트내용")
                .status(ConcertStatus.OPEN)
                .build();

        concertJpaRepository.save(concert);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusDays(5))
                .concertTime(LocalDateTime.now().plusDays(30))
                .build();

        concertScheduleJpaRepository.save(concertSchedule);

        seat = Seat.builder()
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

        request = PaymentHttpDto.PaymentRequestDto.builder()
                .userId(user.getId())
                .reservationId(reservation.getId())
                .build();

    }

    @Test
    void 정상_토큰인_경우_결제_성공() throws Exception {
        //given

        //when//then
        mvc.perform(post("/api/payments")
                        .header("Token", queue.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").value(1L))
                .andExpect(jsonPath("$.amount").value(seat.getSeatPrice()))
                .andExpect(jsonPath("$.paymentStatus").value(PaymentStatus.COMPLETED.toString()));
    }

    @Test
    void 헤더에_토큰이_없는_경우_결제_실패() throws Exception {
        //given

        //when //then
        mvc.perform(post("/api/payments")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("없는 토큰 입니다."));
    }

    @Test
    void 헤더에_토큰이_만료된_경우_결제_실패() throws Exception {
        //given

        //when //then
        mvc.perform(post("/api/payments")
                        .header("Token", expiredQueue.getToken())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않는 토큰 입니다."));
    }

}


