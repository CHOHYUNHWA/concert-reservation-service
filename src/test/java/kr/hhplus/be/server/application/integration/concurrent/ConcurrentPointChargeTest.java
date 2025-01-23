package kr.hhplus.be.server.application.integration.concurrent;

import kr.hhplus.be.server.application.facade.PointFacade;
import kr.hhplus.be.server.domain.entity.Point;
import kr.hhplus.be.server.domain.entity.User;
import kr.hhplus.be.server.infra.repository.jpa.PointJpaRepository;
import kr.hhplus.be.server.infra.repository.jpa.UserJpaRepository;
import kr.hhplus.be.server.interfaces.dto.point.PointHttpDto;
import kr.hhplus.be.server.util.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConcurrentPointChargeTest {

    private Logger log = Logger.getLogger(ConcurrentPointChargeTest.class.getName());

    private Long userId;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private PointFacade pointFacade;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;

    @BeforeEach
    void setUp(){
        databaseCleanUp.execute();

        User user = User.builder()
                .name("TEST")
                .build();

        userJpaRepository.save(user);
        userId = user.getId();

        Point point = Point.builder()
                .userId(user.getId())
                .amount(0L)
                .build();

        pointJpaRepository.save(point);
    }


    @Test
    void 비관적락_사용자가_동시에_여러_번_충전을_요청하면_모두_성공한다() throws InterruptedException {
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
                    pointFacade.chargePointWithPessimisticLock(userId, chargeAmount);
                    successCnt.incrementAndGet();
                } catch (Exception e) {
                    log.warning(e.getMessage());
                    failCnt.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });

        }
        countDownLatch.await();

        PointHttpDto.GetPointResponseDto result = pointFacade.getPoint(userId);

        // 충전 요청 성공 횟수가 스레드 갯수와 같은지 검증한다.
        assertThat(threadCount).isEqualTo(successCnt.intValue());

        // 충전된 금액의 정합성이 보장되는지 검증한다.
        assertThat(chargeAmount * threadCount).isEqualTo(result.getCurrentAmount());
    }


    @Test
    void 낙관적락_사용자가_동시에_여러_번_충전을_요청하면_모두_성공한다() throws InterruptedException {
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
                    pointFacade.chargePointWithOptimisticLock(userId, chargeAmount);
                    successCnt.incrementAndGet();
                } catch (Exception e) {
                    failCnt.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });

        }
        countDownLatch.await();

        PointHttpDto.GetPointResponseDto result = pointFacade.getPoint(userId);

        // 충전 요청 성공 횟수가 스레드 갯수와 같은지 검증한다.
        assertThat(threadCount).isEqualTo(successCnt.intValue());

        // 충전된 금액의 정합성이 보장되는지 검증한다.
        assertThat(chargeAmount * threadCount).isEqualTo(result.getCurrentAmount());
    }



    @Test
    void Redis_분산락_사용자가_동시에_여러_번_충전을_요청하면_모두_성공한다() throws InterruptedException {
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
                    pointFacade.chargePointWithDistributedLock(userId, chargeAmount);
                    successCnt.incrementAndGet();
                } catch (Exception e) {
                    failCnt.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });

        }
        countDownLatch.await();

        PointHttpDto.GetPointResponseDto result = pointFacade.getPoint(userId);

        // 충전 요청 성공 횟수가 스레드 갯수와 같은지 검증한다.
        assertThat(threadCount).isEqualTo(successCnt.intValue());

        // 충전된 금액의 정합성이 보장되는지 검증한다.
        assertThat(chargeAmount * threadCount).isEqualTo(result.getCurrentAmount());
    }

}
