package kr.hhplus.be.server.application.event.payment.spring;

import kr.hhplus.be.server.application.event.payment.PaymentEventListener;
import kr.hhplus.be.server.domain.entity.Payment;
import kr.hhplus.be.server.domain.event.PaymentSuccessEvent;
import kr.hhplus.be.server.infra.dataPlatform.DataPlatformSender;
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
