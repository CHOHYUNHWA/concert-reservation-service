package kr.hhplus.be.server.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.infra.repository.impl.ReservationRepositoryImpl;
import kr.hhplus.be.server.infra.repository.jpa.*;
import kr.hhplus.be.server.interfaces.dto.point.PointHttpDto;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerIntegrationTest {

    private User user;
    private Point point;
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
    private ObjectMapper objectMapper; // SpringBootTest에서 자동 주입

    @BeforeEach
    void setUp() {
        databaseCleanUp.execute();

        user = User.builder()
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

        point = Point.builder()
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
    void Point_조회에_성공() throws Exception {
        //given

        //when //then
        mvc.perform(get("/api/users/{userId}/point", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.currentAmount").value(point.getAmount()));
    }

    @Test
    void 없는_유저의_경우_Point_조회에_실패() throws Exception {
        //given
        Long notExistUserId = 2L;

        //when //then
        mvc.perform(get("/api/users/{userId}/point", notExistUserId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("리소스를 찾을 수 없습니다."));
    }

    @Test
    void Point_충전_성공() throws Exception {
        //given
        Long chargeAmount = 1000L;

        PointHttpDto.ChargePointRequestDto request =
                PointHttpDto.ChargePointRequestDto
                        .builder()
                        .amount(chargeAmount)
                        .build();

        //when //then
        mvc.perform(patch("/api/users/{userId}/point", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.currentPoint").value(chargeAmount + point.getAmount()));
    }

    @Test
    void 없는_유저의_경우_Point_충전_실패() throws Exception {
        //given
        Long notExistUserId = 2L;
        Long chargeAmount = 1000L;

        PointHttpDto.ChargePointRequestDto request =
                PointHttpDto.ChargePointRequestDto
                        .builder()
                        .amount(chargeAmount)
                        .build();

        //when //then
        mvc.perform(patch("/api/users/{userId}/point", notExistUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("리소스를 찾을 수 없습니다."));
    }
}
