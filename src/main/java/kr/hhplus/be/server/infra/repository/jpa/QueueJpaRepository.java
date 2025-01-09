package kr.hhplus.be.server.infra.repository.jpa;

import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.support.type.QueueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueJpaRepository extends JpaRepository<Queue, Long> {
    Optional<Queue> findByToken(String token);

    Long countByStatus(QueueStatus queueStatus);

    Optional<Queue> findTopByStatusOrderByIdDesc(QueueStatus queueStatus);

    List<Queue> findByExpiredAtBeforeAndStatus(LocalDateTime now, QueueStatus queueStatus);

    @Query(value = "SELECT q FROM Queue q WHERE q.status = 'WAITING' ORDER BY q.createdAt ASC")
    Page<Queue> findWaitingTokens(Pageable pageable);
}
