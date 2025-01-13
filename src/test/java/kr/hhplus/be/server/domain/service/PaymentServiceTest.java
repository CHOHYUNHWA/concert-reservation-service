package kr.hhplus.be.server.domain.service;

import kr.hhplus.be.server.domain.entity.Payment;
import kr.hhplus.be.server.domain.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PaymentServiceTest {


    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    void 정상적으로_결제_성공() {
        //given
        Long reservationId = 1L;
        Long userId = 1L;
        Long amount = 1000L;


        Payment expectedPayment = Payment.create(reservationId, userId, amount);

        given(paymentRepository.save(any(Payment.class))).willReturn(expectedPayment);

        //when
        Payment result = paymentService.createPayment(reservationId, userId, amount);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(expectedPayment.getAmount());
        verify(paymentRepository, times(1)).save(any(Payment.class));

    }
}
