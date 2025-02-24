package kr.hhplus.be.server.domain.event.payment;

public interface PaymentEventListener {

    void sendMessageHandler(PaymentEvent event);

    void paymentSuccessHandle(PaymentSuccessEvent event);
}
