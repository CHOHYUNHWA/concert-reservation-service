package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.facade.PointFacade;
import kr.hhplus.be.server.interfaces.dto.Point.PointHttpDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConcurrentPointChargeTest {

    private final Long USER_ID = 1L;

    @Autowired
    private PointFacade pointFacade;


    @Test
    @Transactional
    void 사용자가_동시에_여러_번_충전을_요청하면_모두_성공한다() throws InterruptedException {
        // given
        long chargeAmount = 100L;

        // when
        AtomicInteger successCnt = new AtomicInteger(0);
        AtomicInteger failCnt = new AtomicInteger(0);

        final int threadCount = 100;
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for(int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointFacade.chargePoint(USER_ID, chargeAmount);
                    successCnt.incrementAndGet();
                } catch (Exception e) {
                    failCnt.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });

        }
        countDownLatch.await();

        Thread.sleep(1000);

        PointHttpDto.GetPointResponseDto result = pointFacade.getPoint(USER_ID);

        // 충전 요청 성공 횟수가 스레드 갯수와 같은지 검증한다.
        assertThat(threadCount).isEqualTo(successCnt.intValue());

        // 충전된 금액의 정합성이 보장되는지 검증한다.
        assertThat(chargeAmount * threadCount).isEqualTo(result.getCurrentAmount());
    }
}
