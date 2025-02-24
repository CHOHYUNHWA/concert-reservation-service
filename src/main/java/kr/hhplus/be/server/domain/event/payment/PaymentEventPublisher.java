package kr.hhplus.be.server.domain.event.payment;

public interface PaymentEventPublisher {
    void send(PaymentSuccessEvent event);
}
