package kr.hhplus.be.server.domain.repository;

import kr.hhplus.be.server.domain.entity.Point;

public interface PointRepository {
    Point findPointWithLock(Long userId);

    Point save(Point point);

    Point findPointWithoutLock(Long userId);
}
