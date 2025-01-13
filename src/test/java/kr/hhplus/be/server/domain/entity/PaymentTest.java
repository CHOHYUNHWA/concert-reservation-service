package kr.hhplus.be.server.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentTest {

    @Test
    void 정상적으로_결제가_생성(){
        //given
        Long amount = 5000L;

        //when
        Payment payment = Payment.create(1L, 1L, amount);

        //then
        assertThat(payment).isNotNull();
        assertThat(payment.getAmount()).isEqualTo(amount);

    }
}
