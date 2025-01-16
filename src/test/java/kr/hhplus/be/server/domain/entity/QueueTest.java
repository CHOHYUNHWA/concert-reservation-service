package kr.hhplus.be.server.domain.entity;

import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import kr.hhplus.be.server.support.type.QueueStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.util.Assert.isInstanceOf;


public class QueueTest {

    @Test
    void 입장인원이_30명미만인_경우_토큰이_ACTIVE_토큰_발급_성공(){
        //given
        Long activeCount = 15L;

        //when
        Queue token = Queue.createToken(activeCount);

        //then
        assertThat(token).isNotNull();
        assertThat(token.getStatus()).isEqualTo(QueueStatus.ACTIVE);
    }


    @Test
    void 입장인원이_30명인_경우_토큰이_WAITING_토큰_발급_성공(){
        //given
        Long activeCount = 30L;

        //when
        Queue token = Queue.createToken(activeCount);

        //then
        assertThat(token).isNotNull();
        assertThat(token.getStatus()).isEqualTo(QueueStatus.WAITING);
    }

    @Test
    void 토큰_정상_만료(){
        //given
        Queue token = Queue.builder()
                .status(QueueStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().minusMinutes(5))
                .build();

        token.expiredToken();

        assertThat(token.getStatus()).isEqualTo(QueueStatus.EXPIRED);
        assertThat(token.getExpiredAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void 토큰_상태_체크_ACTIVE면_TRUE_반환(){
        //given
        Queue token = Queue.builder()
                .status(QueueStatus.ACTIVE)
                .build();
        //when
        boolean isActive = token.checkStatus();

        //then
        assertThat(isActive).isTrue();
    }

    @Test
    void 토큰_상태_체크_EXPIRED면_UNAUTHORIZED_에러반환(){
        //given
        Queue token = Queue.builder()
                .status(QueueStatus.EXPIRED)
                .build();
        //when //then
        assertThatThrownBy(token::checkStatus)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.INVALID_TOKEN.getMessage());
    }

    @Test
    void 토큰_검증_시_만료시간이_지난_경우_UNAUTHORIZED_에러반환(){
        //given
        Queue token = Queue.builder()
                .expiredAt(LocalDateTime.now().minusMinutes(5))
                .build();

        //when //then
        assertThatThrownBy(token::validateToken)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.INVALID_TOKEN.getMessage());
    }

}
