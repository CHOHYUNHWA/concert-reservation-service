package kr.hhplus.be.server.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.domain.dto.PaymentEventCommand;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.domain.event.payment.PaymentEvent;
import kr.hhplus.be.server.domain.repository.ReservationRepository;
import kr.hhplus.be.server.domain.service.PointService;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.infra.kafka.producer.KafkaMessageProducer;
import kr.hhplus.be.server.infra.repository.jpa.*;
import kr.hhplus.be.server.interfaces.dto.payment.PaymentHttpDto;
import kr.hhplus.be.server.interfaces.kafka.PaymentMessageConsumer;
import kr.hhplus.be.server.interfaces.kafka.PaymentMessagePayload;
import kr.hhplus.be.server.support.type.ConcertStatus;
import kr.hhplus.be.server.support.type.OutboxStatus;
import kr.hhplus.be.server.support.type.ReservationStatus;
import kr.hhplus.be.server.support.type.SeatStatus;
import kr.hhplus.be.server.util.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
public class KafkaIntegrationTest {

    @Autowired
    PaymentMessageConsumer paymentMessageConsumer;

    @Autowired
    KafkaMessageProducer kafkaMessageProducer;

    private User user;
    private Queue queue;
    private Concert concert;
    private ConcertSchedule concertSchedule;
    private Seat seat;
    private Reservation reservation;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private PaymentFacade paymentFacade;
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
    private ReservationRepository reservationRepository;
    @Autowired
    private PointService pointService;
    @Autowired
    private QueueService queueService;

    @BeforeEach
    void setUp() {

        databaseCleanUp.execute();

        user = User.builder()
                .name("유저")
                .build();

        userJpaRepository.save(user);

        queue = queueService.createToken();

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

        seat = Seat.builder()
                .seatNumber(1L)
                .seatPrice(100L)
                .seatStatus(SeatStatus.AVAILABLE)
                .reservedAt(null)
                .concertScheduleId(concertSchedule.getId())
                .build();

        seatJpaRepository.save(seat);

        reservation = Reservation.builder()
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
    void 카프카_메시지_발행시_소비_성공() throws InterruptedException, JsonProcessingException {
        //given
        PaymentEventCommand paymentEventCommand = PaymentEventCommand.builder()
                .id(1L)
                .reservationId(1L)
                .amount(1000L)
                .userId(1L)
                .paymentAt(LocalDateTime.now())
                .uuid(UUID.randomUUID().toString())
                .build();

        PaymentEvent paymentEvent = PaymentEvent.from("concert-payment", OutboxStatus.PROCESSED, paymentEventCommand);

        //when
        kafkaMessageProducer.send(paymentEvent);

        //then
        // then - 5초 이내에 Consumer가 메시지를 정상적으로 수신했는지 확인
        await().atMost(5, SECONDS).untilAsserted(() ->
                assertThat(paymentMessageConsumer.getLastReceivedMessage()).isNotNull()
        );

        // 메시지 내용 검증
        PaymentMessagePayload receivedMessage = paymentMessageConsumer.getLastReceivedMessage();
        assertThat(receivedMessage.getUserId()).isEqualTo(paymentEventCommand.getUserId());
        assertThat(receivedMessage.getAmount()).isEqualTo(paymentEventCommand.getAmount());
    }


    @Test
    void 결제_시_Outbox_저장_성공(){

        //given
        pointService.chargePointWithoutLock(user.getId(), 10_000L);

        //when
        PaymentHttpDto.PaymentCompletedResponse payment = paymentFacade.paymentWithPessimisticLockWithKafka(queue.getToken(), reservation.getId(), user.getId());

        await().atMost(5, SECONDS).untilAsserted(() ->
                assertThat(paymentMessageConsumer.getLastReceivedMessage()).isNotNull()
        );
    }

}
