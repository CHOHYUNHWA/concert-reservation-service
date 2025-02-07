package kr.hhplus.be.server.application.integration;

import kr.hhplus.be.server.application.facade.QueueFacade;
import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.entity.User;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.infra.repository.jpa.UserJpaRepository;
import kr.hhplus.be.server.interfaces.dto.queue.QueueHttpDto;
import kr.hhplus.be.server.support.type.QueueStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class QueueFacadeTest {

    @Autowired
    private QueueFacade queueFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @MockitoSpyBean
    private QueueRepository queueRepository;

    @Test
    void 토큰을_생성한다() {
        //given
        User user = User.builder()
                .name("이름")
                .build();

        User savedUser = userJpaRepository.save(user);

        //when
        Queue token = queueFacade.createToken(savedUser.getId());

        //then
        assertThat(token).isNotNull();
        assertThat(token.getStatus()).isEqualTo(QueueStatus.ACTIVE);
    }

    @Test
    void 동시에_여러번_대기열을_조회_시_첫번째만_Redis에서_조회_하고_이후_캐시에서_조회에_성공(){
        //given
        User user = User.builder()
                .name("이름")
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
