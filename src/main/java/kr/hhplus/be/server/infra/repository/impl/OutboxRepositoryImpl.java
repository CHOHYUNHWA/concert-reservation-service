package kr.hhplus.be.server.infra.repository.impl;

import kr.hhplus.be.server.domain.entity.Outbox;
import kr.hhplus.be.server.domain.event.outbox.OutboxEvent;
import kr.hhplus.be.server.domain.repository.OutboxRepository;
import kr.hhplus.be.server.infra.repository.jpa.OutboxJpaRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
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

    @Override
    public Outbox findByUuid(String uuid) {
        return outboxJpaRepository.findByUuid(uuid).orElseThrow(() -> new CustomException(ErrorType.RESOURCE_NOT_FOUND, "uuid: " + uuid));
    }

}
