package kr.hhplus.be.server.domain.entity;

import kr.hhplus.be.server.support.type.ConcertStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcertTest {

    @Test
    void 콘서트_상태를_조회한다_OPEN일_경우_TRUE(){
        //given
        Concert concert = Concert.builder()
                .status(ConcertStatus.OPEN)
                .build();

        //when
        boolean isOpened = concert.checkStatus();

        //then
        assertThat(isOpened).isTrue();
    }

    @Test
    void 콘서트_상태를_조회한다_CLOSE일_경우_FALSE(){
        //given
        Concert concert = Concert.builder()
                .status(ConcertStatus.CLOSED)
                .build();

        //when
        boolean isOpened = concert.checkStatus();

        //then
        assertThat(isOpened).isFalse();
    }
}
