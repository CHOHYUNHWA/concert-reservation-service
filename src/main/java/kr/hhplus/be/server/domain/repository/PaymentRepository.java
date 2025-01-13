package kr.hhplus.be.server.domain.repository;

import kr.hhplus.be.server.domain.entity.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
}
