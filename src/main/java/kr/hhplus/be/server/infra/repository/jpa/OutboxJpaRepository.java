package kr.hhplus.be.server.infra.repository.jpa;

import kr.hhplus.be.server.domain.entity.Outbox;
import kr.hhplus.be.server.domain.event.outbox.OutboxEvent;
import kr.hhplus.be.server.support.type.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {
    List<Outbox> findByStatusNot(String outboxStatus);

    Optional<Outbox> findByUuid(String uuid);
}
