package kr.hhplus.be.server.application.integration.caching;

import kr.hhplus.be.server.application.facade.QueueFacade;
import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.entity.User;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.infra.repository.jpa.UserJpaRepository;
import kr.hhplus.be.server.interfaces.dto.queue.QueueHttpDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class QueueFacadeCachingTest {

    private Logger log = LoggerFactory.getLogger(QueueFacadeCachingTest.class);

    @Autowired
    private QueueFacade queueFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @MockitoSpyBean
    private QueueRepository queueRepository;

    @Test
    void _10초_내_대기열_조회_여러번_조회_시_1번만_Redis_접근_후_캐싱데이터_로드_성공(){
        //given
        User user = User
                .builder()
                .name("유저")
                .build();

        User savedUser = userJpaRepository.save(user);
        Queue token = queueFacade.createToken(savedUser.getId());

        doReturn(token).when(queueRepository).findToken(token.getToken());
        doReturn(0L).when(queueRepository).getWaitingRank(token.getToken());


        //when
        QueueHttpDto.QueueStatusResponseDto queueRemainingCount1 = queueFacade.getQueueRemainingCount(token.getToken(), savedUser.getId());
        QueueHttpDto.QueueStatusResponseDto queueRemainingCount2 = queueFacade.getQueueRemainingCount(token.getToken(), savedUser.getId());

        //then
        verify(queueRepository, times(1)).findToken(token.getToken());
        verify(queueRepository, times(1)).getWaitingRank(token.getToken());
    }
}
