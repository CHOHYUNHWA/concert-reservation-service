package kr.hhplus.be.server.infra.repository.jpa;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<Point, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Point p where p.userId = ?1")
    Optional<Point> findByUserIdWithPessimisticLock(Long userId);

    @Query("select p from Point p where p.userId = ?1")
    Optional<Point> findByUserIdWithoutLock(Long userId);
}
