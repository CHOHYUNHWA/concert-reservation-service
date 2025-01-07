package kr.hhplus.be.server.infra.repository.jpa;

import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.support.type.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QueueJpaRepository extends JpaRepository<Queue, Long> {
    Optional<Queue> findByToken(String token);

    Long countByStatus(QueueStatus queueStatus);

    Optional<Queue> findTopByStatusOrderByIdDesc(QueueStatus queueStatus);
}
