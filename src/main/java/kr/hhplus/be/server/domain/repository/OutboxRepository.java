package kr.hhplus.be.server.domain.repository;

import kr.hhplus.be.server.domain.entity.Outbox;
import kr.hhplus.be.server.domain.event.OutBoxEvent;

public interface OutboxRepository {

    Outbox save(OutBoxEvent outBoxEvent);
}
