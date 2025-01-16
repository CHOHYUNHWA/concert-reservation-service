package kr.hhplus.be.server.infra.repository.impl;

import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.infra.repository.jpa.QueueJpaRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import kr.hhplus.be.server.support.type.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {

    private final QueueJpaRepository queueJpaRepository;

    @Override
    public Queue findQueue(String token) {
        return queueJpaRepository.findByToken(token).orElseThrow(() -> new CustomException(ErrorType.TOKEN_NOT_FOUND, "토큰: " + token));
    }

    @Override
    public Long countByStatus(QueueStatus queueStatus) {
        return queueJpaRepository.countByStatus(queueStatus);
    }

    @Override
    public Queue save(Queue token) {
        return queueJpaRepository.save(token);
    }

    @Override
    public Long findLatestActiveQueueIdByStatus(Long queueId) {
        Optional<Queue> latestActiveQueue = queueJpaRepository.findTopByStatusOrderByIdDesc(QueueStatus.ACTIVE);

        return latestActiveQueue.map(Queue::getId) // 값이 있으면 id 반환
                .orElse(queueId);
    }

    @Override
    public List<Queue> findExpiredTokens(LocalDateTime now, QueueStatus queueStatus) {
        return queueJpaRepository.findByExpiredAtBeforeAndStatus(now, queueStatus);
    }

    @Override
    public List<Queue> findWaitingTokens(long forChangeTokenCount) {
        Pageable pageable = PageRequest.of(0, (int) forChangeTokenCount);
        return queueJpaRepository.findWaitingTokens(pageable).getContent();
    }
}
