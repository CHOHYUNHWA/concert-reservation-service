package kr.hhplus.be.server.domain.entity;

import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PointTest {

    @Test
    void 정상_포인트_충전_성공(){
        //given
        Long currentAmount = 500L;
        Long chargeAmount = 1000L;
        Point point = Point.builder()
                .id(1L)
                .userId(1L)
                .amount(currentAmount)
                .updatedAt(LocalDateTime.now())
                .build();

        //when
        Point chargedPoint = point.charge(chargeAmount);


        //then
        assertThat(chargedPoint).isNotNull();
        assertThat(chargedPoint.getAmount()).isEqualTo(currentAmount+chargeAmount);
    }

    @Test
    void _0원_미만_포인트_충전시_에러(){
        //given
        Long currentAmount = 500L;
        Long chargeAmount = 0L;
        Point point = Point.builder()
                .id(1L)
                .userId(1L)
                .amount(currentAmount)
                .updatedAt(LocalDateTime.now())
                .build();

        //when //then
        assertThatThrownBy(() -> point.charge(chargeAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AMOUNT.getMessage());
    }

    @Test
    void 정상_포인트_사용_성공(){
        //given
        Long currentAmount = 1000L;
        Long useAmount = 500L;

        Point point = Point.builder()
                .id(1L)
                .userId(1L)
                .amount(currentAmount)
                .updatedAt(LocalDateTime.now())
                .build();

        //when
        Point usedPoint = point.usePoint(useAmount);

        //then
        assertThat(usedPoint).isNotNull();
        assertThat(usedPoint.getAmount()).isEqualTo(currentAmount-useAmount);
    }

    @Test
    void _0원_미만_또는_포인트_사용시_에러(){
        //given
        Long currentAmount = 500L;
        Long useAmount = 0L;
        Point point = Point.builder()
                .id(1L)
                .userId(1L)
                .amount(currentAmount)
                .updatedAt(LocalDateTime.now())
                .build();

        //when //then
        assertThatThrownBy(() -> point.usePoint(useAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AMOUNT.getMessage());
    }

    @Test
    void 보유_포인트_보다__초과_사용시_에러(){
        //given
        Long currentAmount = 500L;
        Long useAmount = 1000L;
        Point point = Point.builder()
                .id(1L)
                .userId(1L)
                .amount(currentAmount)
                .updatedAt(LocalDateTime.now())
                .build();

        //when //then
        assertThatThrownBy(() -> point.usePoint(useAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AMOUNT.getMessage());
    }
}
