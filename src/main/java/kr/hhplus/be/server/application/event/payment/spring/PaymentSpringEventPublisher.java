package kr.hhplus.be.server.application.event.payment.spring;

import kr.hhplus.be.server.application.event.payment.PaymentEventPublisher;
import kr.hhplus.be.server.domain.entity.Payment;
import kr.hhplus.be.server.domain.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSpringEventPublisher implements PaymentEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Async
    public void send(PaymentSuccessEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
