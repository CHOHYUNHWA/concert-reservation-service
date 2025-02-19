package kr.hhplus.be.server.domain.service;

import kr.hhplus.be.server.domain.dto.PaymentEventCommand;
import kr.hhplus.be.server.domain.event.payment.PaymentEvent;
import kr.hhplus.be.server.support.type.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventService {

    @Value("${event.payment.topic}")
    private String topic;

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(PaymentEventCommand command) {
        try{
            applicationEventPublisher.publishEvent(PaymentEvent.from(topic, OutboxStatus.INIT, command));
        } catch (Exception e){
            log.error("Payment event 발행 실패",e);
        }

    }
}
