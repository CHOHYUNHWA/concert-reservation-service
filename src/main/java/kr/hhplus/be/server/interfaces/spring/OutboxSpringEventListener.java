package kr.hhplus.be.server.interfaces.spring;

import kr.hhplus.be.server.domain.event.outbox.OutboxEventListener;
import kr.hhplus.be.server.domain.event.payment.PaymentEvent;
import kr.hhplus.be.server.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxSpringEventListener implements OutboxEventListener {

    private final OutboxRepository outboxRepository;

    @Override
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void paymentInitHandler(PaymentEvent event) {
        log.info("paymentInitHandler: {}", event);
        outboxRepository.save(event);
    }

}
