package kr.hhplus.be.server.domain.scheduler;

import kr.hhplus.be.server.application.facade.QueueFacade;
import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.entity.User;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.domain.repository.UserRepository;
import kr.hhplus.be.server.infra.repository.impl.QueueRepositoryImpl;
import kr.hhplus.be.server.infra.repository.jpa.UserJpaRepository;
import kr.hhplus.be.server.interfaces.dto.queue.QueueHttpDto;
import kr.hhplus.be.server.interfaces.scheduler.TokenScheduler;
import kr.hhplus.be.server.support.type.QueueStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TokenSchedulerTest {

    private Logger log = LoggerFactory.getLogger(TokenSchedulerTest.class);

    @Autowired
    private TokenScheduler tokenScheduler;

    @Autowired
    private QueueFacade queueFacade;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void 활성토큰이_30개_미만인_경우_WaitingToken이_ActiveToken으로_전환_성공(){
        //given
        User user = User
                .builder()
                .name("유저")
                .build();

        User savedUser = userJpaRepository.save(user);

        for(long l = 0; l< 30; l++){
            queueFacade.createToken(savedUser.getId());
        }

        Long prevActiveTokenCount = queueRepository.getActiveTokenCount();
        log.info("토큰 생성 직후 activeTokenCount: {}", prevActiveTokenCount);

        queueRepository.removeOldestTwoActiveTokens();

        Long nextActiveTokenCount = queueRepository.getActiveTokenCount();
        log.info("2개 토큰 삭제 직후 activeTokenCount: {}", nextActiveTokenCount);
        Queue waitingToken = queueFacade.createToken(savedUser.getId());
        System.out.println("생성된 토큰 상태: " + waitingToken.getStatus());

        //when
        tokenScheduler.updateActiveToken();

        //then
        assertThat(waitingToken.getStatus()).isEqualTo(QueueStatus.ACTIVE);
    }

    @Test
    void 만료시간이_지난_활성토큰의_경우_Redis에서_삭제_성공(){
        //given
        User user = User
                .builder()
                .name("유저")
                .build();

        User savedUser = userJpaRepository.save(user);
        Queue token = queueFacade.createToken(savedUser.getId());
        Long prevActiveTokenCount = queueRepository.getActiveTokenCount();
        log.info("토큰 생성 직후 activeTokenCount: {}", prevActiveTokenCount);

        queueRepository.expiredActiveToken(token.getToken());

        //when
        tokenScheduler.removeExpiredTokensScheduler();
        Long nextActiveTokenCount = queueRepository.getActiveTokenCount();
        log.info("토큰 스케쥴러 만료처리 직후 activeTokenCount: {}", nextActiveTokenCount);

        //then
        assertThat(queueRepository.activeTokenExist(token.getToken())).isFalse();
    }
}
