package kr.hhplus.be.server.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.domain.dto.PaymentEventCommand;
import kr.hhplus.be.server.domain.event.payment.PaymentEvent;
import kr.hhplus.be.server.infra.kafka.producer.KafkaMessageProducer;
import kr.hhplus.be.server.interfaces.kafka.PaymentMessageConsumer;
import kr.hhplus.be.server.interfaces.kafka.PaymentMessagePayload;
import kr.hhplus.be.server.support.type.OutboxStatus;
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
        Thread.sleep(30000);
        // then - 5초 이내에 Consumer가 메시지를 정상적으로 수신했는지 확인
        await().atMost(5, SECONDS).untilAsserted(() ->
                assertThat(paymentMessageConsumer.getLastReceivedMessage()).isNotNull()
        );

        // 메시지 내용 검증
        PaymentMessagePayload receivedMessage = paymentMessageConsumer.getLastReceivedMessage();
        assertThat(receivedMessage.getUserId()).isEqualTo(paymentEventCommand.getUserId());
        assertThat(receivedMessage.getAmount()).isEqualTo(paymentEventCommand.getAmount());
    }

}
