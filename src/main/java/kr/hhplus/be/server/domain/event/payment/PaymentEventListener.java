package kr.hhplus.be.server.domain.event.payment;

public interface PaymentEventListener {

    void paymentSuccessHandle(PaymentSuccessEvent event);
}
