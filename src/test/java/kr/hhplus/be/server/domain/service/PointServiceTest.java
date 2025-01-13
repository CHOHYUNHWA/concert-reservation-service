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

        given(pointRepository.findPoint(userId)).willReturn(point);

        //when
        Point result = pointService.getPoint(userId);

        //then
        assertThat(result).isEqualTo(point);
        verify(pointRepository, times(1)).findPoint(userId);
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
        Point usedPoint = point.usePoint(useAmount);

        Long expectedAmount = point.getAmount() - useAmount;

        given(pointRepository.findPoint(userId)).willReturn(point);
        given(pointRepository.save(point)).willReturn(usedPoint);

        //when
        Point result = pointService.usePoint(userId, useAmount);

        //then
        assertThat(result.getAmount()).isEqualTo(expectedAmount);
        verify(pointRepository, times(1)).findPoint(userId);
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
        Point chargedPoint = point.charge(chargeAmount);

        Long expectedAmount = point.getAmount() + chargeAmount;

        given(pointRepository.findPoint(userId)).willReturn(point);
        given(pointRepository.save(point)).willReturn(chargedPoint);
        //when

        Point result = pointService.chargePoint(userId, chargeAmount);

        //then
        assertThat(result.getAmount()).isEqualTo(expectedAmount);
        verify(pointRepository, times(1)).findPoint(userId);
        verify(pointRepository, times(1)).save(point);
    }

}
