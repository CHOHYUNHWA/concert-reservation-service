package kr.hhplus.be.server.interfaces.scheduler;


import kr.hhplus.be.server.domain.entity.Outbox;
import kr.hhplus.be.server.domain.event.payment.PaymentEvent;
import kr.hhplus.be.server.domain.repository.OutboxRepository;
import kr.hhplus.be.server.infra.kafka.producer.KafkaMessageProducer;
import kr.hhplus.be.server.support.type.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxRetryScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaMessageProducer kafkaMessageProducer;

    @Scheduled(fixedDelay = 5000)
    public void retryFailedOutboxEvents(){
        List<Outbox> failedEvents = outboxRepository.findByStatusNot(OutboxStatus.PROCESSED.name());

        for (Outbox failedEvent : failedEvents) {
            if(failedEvent.getStatus().equals(OutboxStatus.INIT.name()) && failedEvent.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1))){
                return;
            }
            kafkaMessageProducer.send(PaymentEvent.mapToPaymentEvent(failedEvent.of()));
        }
    }
}
