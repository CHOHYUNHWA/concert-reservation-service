package kr.hhplus.be.server.domain.service;

import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import kr.hhplus.be.server.support.type.QueueStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QueueServiceTest {

    @InjectMocks
    private QueueService queueService;

    @Mock
    private QueueRepository queueRepository;

    @Test
    void 토큰_생성_성공(){
        //given
        given(queueRepository.countByStatus(QueueStatus.ACTIVE)).willReturn(10L);
        Queue expectedQueue = Queue.createToken(10L);
        given(queueRepository.save(any(Queue.class)))
                .willReturn(expectedQueue);

        //when
        Queue token = queueService.createToken();

        //then
        assertThat(token)
                .usingRecursiveComparison()
                .ignoringFields("token", "createdAt")
                .isEqualTo(expectedQueue);
        verify(queueRepository, times(1)).save(any(Queue.class));

    }

    @Test
    void 토큰_만료_성공(){
        //given
        Queue token = mock(Queue.class);
        Queue expiredToken = mock(Queue.class);
        given(token.expiredToken()).willReturn(expiredToken);
        //when
        queueService.expireToken(token);

        //then
        verify(queueRepository, times(1)).expireToken(expiredToken);

    }

    @Test
    void 토큰_상태체크_성공(){
        //given
        Queue activeToken = Queue.builder()
                .status(QueueStatus.ACTIVE)
                .build();
        //when
        boolean isActive = queueService.checkQueueStatus(activeToken);

        //then
        assertThat(isActive).isTrue();
    }

    @Test
    void 토큰_상태체크시_EXPIRED_토큰의_경우_예외_던짐(){
        //given
        Queue expiredToken = Queue.builder()
                .status(QueueStatus.EXPIRED)
                .build();
        //when //then
        CustomException exception = assertThrows(CustomException.class,
                () -> queueService.checkQueueStatus(expiredToken));

        assertThat(exception.getErrorType()).isEqualTo(ErrorType.INVALID_TOKEN);
    }

    @Test
    void 대기열_조회시_존재하는_대기열이_있을때_순번_반환_성공(){
        //given
        Queue queue = Queue.builder()
                .id(100L)
                .status(QueueStatus.WAITING)
                .build();
        given(queueRepository.findLatestActiveQueueIdByStatus(queue.getId())).willReturn(40L);
        //when
        Long remainingQueueCount = queueService.checkRemainingQueueCount(queue);

        //then
        assertThat(remainingQueueCount).isEqualTo(60L);
        verify(queueRepository, times(1)).findLatestActiveQueueIdByStatus(queue.getId());
    }

    @Test
    void 대기열_조회시_존재하는_대기열이_없을시_0을_반환(){
        //given
        Queue queue = Queue.builder()
                .id(100L)
                .status(QueueStatus.WAITING)
                .build();
        given(queueRepository.findLatestActiveQueueIdByStatus(queue.getId())).willReturn(100L);
        //when
        Long remainingQueueCount = queueService.checkRemainingQueueCount(queue);

        //then
        assertThat(remainingQueueCount).isEqualTo(0L);
        verify(queueRepository, times(1)).findLatestActiveQueueIdByStatus(queue.getId());
    }

    @Test
    void 토큰_유효성_검증_시_만료_시간_초과시_예외_발생(){
        //given
        String token = "testToken";
        Queue queue = mock(Queue.class);
        given(queueRepository.findQueue(token)).willReturn(queue);
        doThrow(new CustomException(ErrorType.INVALID_TOKEN, "현재 토큰 상태: " +queue.getStatus())).when(queue).validateToken();

        //when
        //then
        assertThrows(CustomException.class, () -> queueService.validateToken(token));
    }

    @Test
    void 토큰_유효성_검증_성공(){
        //given
        String token = "testToken";
        Queue queue = mock(Queue.class);
        given(queueRepository.findQueue(token)).willReturn(queue);

        //when
        Queue result = queueService.validateToken(token);

        //then
        assertThat(result).isEqualTo(queue);
    }
}
