package kr.hhplus.be.server.application.event.payment;

import kr.hhplus.be.server.domain.event.PaymentSuccessEvent;

public interface PaymentEventListener {

    void paymentSuccessHandle(PaymentSuccessEvent event);
}
