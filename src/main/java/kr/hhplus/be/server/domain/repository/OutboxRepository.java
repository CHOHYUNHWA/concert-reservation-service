package kr.hhplus.be.server.domain.repository;

import kr.hhplus.be.server.domain.entity.Outbox;
import kr.hhplus.be.server.domain.event.OutboxEvent;

import java.util.List;

public interface OutboxRepository {

    Outbox save(OutboxEvent outBoxEvent);

    List<Outbox> findByStatusNot(String outboxStatus);
}
