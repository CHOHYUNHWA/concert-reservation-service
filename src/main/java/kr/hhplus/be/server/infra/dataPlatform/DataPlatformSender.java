package kr.hhplus.be.server.infra.dataPlatform;

import kr.hhplus.be.server.domain.event.PaymentSuccessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//외부 가정하에 빈으로 등록
@Service
@Slf4j
public class DataPlatformSender {

    public void sendData(PaymentSuccessEvent event) throws InterruptedException {
        Thread.sleep(1000L);
        log.info("데이터 전송완료 -  예약 ID: {}, 결제금액: {}", event.getReservationId(), event.getAmount());
    }
}
