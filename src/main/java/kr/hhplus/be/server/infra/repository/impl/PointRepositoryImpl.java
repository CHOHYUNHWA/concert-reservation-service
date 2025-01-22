package kr.hhplus.be.server.infra.repository.impl;

import kr.hhplus.be.server.domain.entity.Point;
import kr.hhplus.be.server.domain.entity.User;
import kr.hhplus.be.server.domain.repository.PointRepository;
import kr.hhplus.be.server.infra.repository.jpa.PointJpaRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Point findPointWithLock(Long userId) {
        return pointJpaRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }
}
