package kr.hhplus.be.server.domain.repository;

import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.support.type.QueueStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface QueueRepository {
    Queue findQueue(String token);

    Long countByStatus(QueueStatus queueStatus);

    Queue save(Queue token);

    void expireToken(Queue expiredToken);

    Long findLatestActiveQueueIdByStatus(Long queueId);

    List<Queue> findExpiredTokens(LocalDateTime now, QueueStatus queueStatus);

    List<Queue> findWaitingTokens(long forChangeTokenCount);
}

