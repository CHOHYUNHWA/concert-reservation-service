package kr.hhplus.be.server.domain.repository;

import kr.hhplus.be.server.domain.entity.Point;

public interface PointRepository {
    Point findPoint(Long userId);

    Point save(Point point);
}
