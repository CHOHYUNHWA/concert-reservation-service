package kr.hhplus.be.server.infra.repository.impl;

import kr.hhplus.be.server.domain.entity.Outbox;
import kr.hhplus.be.server.domain.event.outbox.OutboxEvent;
import kr.hhplus.be.server.domain.repository.OutboxRepository;
import kr.hhplus.be.server.infra.repository.jpa.OutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    public Outbox save(OutboxEvent outBoxEvent) {
        return outboxJpaRepository.save(Outbox.from(outBoxEvent));
    }

    @Override
    public List<Outbox> findByStatusNot(String outboxStatus) {
        return outboxJpaRepository.findByStatusNot(outboxStatus);
    }

}
