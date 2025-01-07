package kr.hhplus.be.server.domain.repository;

import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.support.type.QueueStatus;

public interface QueueRepository {
    Queue findQueue(String token);

    Long countByStatus(QueueStatus queueStatus);

    Queue save(Queue token);

    void expireToken(Queue expiredToken);

    Long findLatestActiveQueueIdByStatus(Long queueId);
}

