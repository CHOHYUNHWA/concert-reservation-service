package kr.hhplus.be.server.interfaces.spring;

import kr.hhplus.be.server.domain.event.payment.PaymentEvent;
import kr.hhplus.be.server.domain.event.payment.PaymentEventListener;
import kr.hhplus.be.server.domain.event.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.repository.OutboxRepository;
import kr.hhplus.be.server.infra.dataPlatform.DataPlatformSender;
import kr.hhplus.be.server.infra.kafka.producer.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSpringEventListener implements PaymentEventListener {

    private final DataPlatformSender dataPlatformSender;
    private final OutboxRepository outboxRepository;
    private final KafkaMessageProducer kafkaMessageProducer;


    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void paymentInitHandler(PaymentEvent event) {
        log.info("paymentInitHandler: {}", event);
        outboxRepository.save(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendMessageHandler(PaymentEvent event){
        kafkaMessageProducer.send(event);
        log.info("Send event to Kafka: topic={}, key={}, payload={}", event.getTopic(), event.getKey(), event.getPayload());
    }



    @Override
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandle(PaymentSuccessEvent event) {
        try{
            dataPlatformSender.sendData(event);
        } catch (Exception e){
            log.error("데이터 플랫폼 전송 실패", e);
        }
    }
}
