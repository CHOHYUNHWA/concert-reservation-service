package kr.hhplus.be.server.domain.event.outbox;

import kr.hhplus.be.server.domain.event.payment.PaymentEvent;

public interface OutboxEventListener {

    void paymentInitHandler(PaymentEvent event);
}
