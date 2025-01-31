package kr.hhplus.be.server.domain.service;

import kr.hhplus.be.server.domain.entity.Point;
import kr.hhplus.be.server.domain.repository.PointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;


    @Test
    void 포인트_정상_조회(){
        //given
        Long userId = 1L;
        Point point = Point.builder()
                .id(1L)
                .userId(userId)
                .amount(1000L)
                .updatedAt(LocalDateTime.now())
                .build();

        given(pointRepository.findByUserIdWithoutLock(userId)).willReturn(point);

        //when
        Point result = pointService.getPointWithoutLock(userId);

        //then
        assertThat(result).isEqualTo(point);
        verify(pointRepository, times(1)).findByUserIdWithoutLock(userId);
    }

    @Test
    void 포인트_정상_사용(){
        //given
        Long userId = 1L;
        Point point = Point.builder()
                .id(1L)
                .userId(userId)
                .amount(1000L)
                .updatedAt(LocalDateTime.now())
                .build();

        Long useAmount = 400L;
        Long expectedAmount = point.getAmount() - useAmount;

        given(pointRepository.findByUserIdWithPessimisticLock(userId)).willReturn(point);
        given(pointRepository.save(point)).willReturn(point);

        //when
        Point result = pointService.usePoint(userId, useAmount);

        //then
        assertThat(result.getAmount()).isEqualTo(expectedAmount);
        verify(pointRepository, times(1)).findByUserIdWithPessimisticLock(userId);
        verify(pointRepository, times(1)).save(point);
    }


    @Test
    void 포인트_정상_충전(){
        //given
        Long userId = 1L;
        Point point = Point.builder()
                .id(1L)
                .userId(userId)
                .amount(1000L)
                .updatedAt(LocalDateTime.now())
                .build();

        Long chargeAmount = 2000L;
        point.charge(chargeAmount);

        Long expectedAmount = point.getAmount() + chargeAmount;

        given(pointRepository.findByUserIdWithoutLock(userId)).willReturn(point);
        given(pointRepository.save(point)).willReturn(point);
        //when

        Point result = pointService.chargePointWithoutLock(userId, chargeAmount);

        //then
        assertThat(result.getAmount()).isEqualTo(expectedAmount);
        verify(pointRepository, times(1)).findByUserIdWithoutLock(userId);
        verify(pointRepository, times(1)).save(point);
    }

}
