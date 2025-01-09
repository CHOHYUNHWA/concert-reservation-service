package kr.hhplus.be.server.infra.repository.impl;

import kr.hhplus.be.server.domain.entity.Payment;
import kr.hhplus.be.server.domain.repository.PaymentRepository;
import kr.hhplus.be.server.infra.repository.jpa.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
