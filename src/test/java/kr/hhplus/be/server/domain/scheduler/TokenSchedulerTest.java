package kr.hhplus.be.server.domain.scheduler;

import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.support.type.QueueStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TokenSchedulerTest {

    @InjectMocks
    private TokenScheduler tokenScheduler;

    @Mock
    private QueueRepository queueRepository;

    @Test
    void 만료시간이_지난_토큰들을_일괄_만료_변경(){
        //given
        LocalDateTime now = LocalDateTime.now();
        Queue token1 = Queue.builder()
                .token("1번토큰")
                .status(QueueStatus.ACTIVE)
                .createdAt(now.minusMinutes(5))
                .enteredAt(now.minusMinutes(5))
                .expiredAt(now.minusMinutes(5))
                .build();

        Queue token2 = Queue.builder()
                .token("1번토큰")
                .status(QueueStatus.ACTIVE)
                .createdAt(now.minusMinutes(5))
                .enteredAt(now.minusMinutes(5))
                .expiredAt(now.minusMinutes(5))
                .build();

        List<Queue> tokens = List.of(token1, token2);

        given(queueRepository.findExpiredTokens(any(LocalDateTime.class), eq(QueueStatus.ACTIVE))).willReturn(tokens);

        //when
        tokenScheduler.expireTokens();

        //then
        verify(queueRepository, times(1)).findExpiredTokens(any(LocalDateTime.class), eq(QueueStatus.ACTIVE));
        verify(queueRepository, times(2)).save(argThat(savedToken ->
                savedToken.getStatus().equals(QueueStatus.EXPIRED)));

    }


    @Test
    void 부족한_ACTIVE_토큰_수_만큼_활성_상태_변경(){
        //given
        LocalDateTime now = LocalDateTime.now();

        Queue token1 = Queue.builder()
                .token("1번토큰")
                .status(QueueStatus.ACTIVE)
                .createdAt(now.minusMinutes(5))
                .build();

        Queue token2 = Queue.builder()
                .token("1번토큰")
                .status(QueueStatus.ACTIVE)
                .createdAt(now.minusMinutes(5))
                .build();

        List<Queue> tokens = List.of(token1, token2);

        given(queueRepository.countByStatus(QueueStatus.ACTIVE)).willReturn(28L);
        given(queueRepository.findWaitingTokens(2L)).willReturn(tokens);


        //when
        tokenScheduler.changeActiveTokens();

        //then
        verify(queueRepository, times(1)).countByStatus(QueueStatus.ACTIVE);
        verify(queueRepository, times(1)).findWaitingTokens(2L); // 필요한 수만큼 조회
        verify(queueRepository, times(2)).save(argThat(savedToken ->
                savedToken.getStatus() == QueueStatus.ACTIVE
        ));
    }
}
