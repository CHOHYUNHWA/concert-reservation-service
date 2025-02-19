package kr.hhplus.be.server.infra.repository.jpa;

import kr.hhplus.be.server.domain.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {
}
