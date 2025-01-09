package kr.hhplus.be.server.domain.service;

import kr.hhplus.be.server.domain.entity.Payment;
import kr.hhplus.be.server.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment createPayment(Long reservationId, Long userId, Long amount) {
        Payment payment = Payment.create(reservationId, userId, amount);
        return paymentRepository.save(payment);

    }
}
