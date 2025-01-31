package kr.hhplus.be.server.domain.repository;

import kr.hhplus.be.server.domain.entity.Point;

public interface PointRepository {
    Point findByUserIdWithPessimisticLock(Long userId);

    Point save(Point point);

    Point findByUserIdWithoutLock(Long userId);

    Point findByUserIdWithOptimisticLock(Long userId);
}
