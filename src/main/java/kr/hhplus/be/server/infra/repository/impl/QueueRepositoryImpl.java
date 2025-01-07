package kr.hhplus.be.server.infra.repository.impl;

import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.infra.repository.jpa.QueueJpaRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorCode;
import kr.hhplus.be.server.support.type.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {

    private final QueueJpaRepository queueJpaRepository;

    @Override
    public Queue findQueue(String token) {
        return queueJpaRepository.findByToken(token).orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));
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
    public void expireToken(Queue expiredToken) {
        queueJpaRepository.save(expiredToken);
    }

    @Override
    public Long findLatestActiveQueueIdByStatus(Long queueId) {
        Optional<Queue> latestActiveQueue = queueJpaRepository.findTopByStatusOrderByIdDesc(QueueStatus.ACTIVE);

        return latestActiveQueue.map(Queue::getId) // 값이 있으면 id 반환
                .orElse(queueId);
    }
}
