package kr.hhplus.be.server.infra.repository.impl;

import kr.hhplus.be.server.domain.entity.Point;
import kr.hhplus.be.server.domain.repository.PointRepository;
import kr.hhplus.be.server.infra.repository.jpa.PointJpaRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Point findPointWithLock(Long userId) {
        return pointJpaRepository.findByUserIdWithPessimisticLock(userId).orElseThrow(() -> new CustomException(ErrorType.RESOURCE_NOT_FOUND, "검색한 USER ID: "+ userId));
    }

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }

    @Override
    public Point findPointWithoutLock(Long userId) {
        return pointJpaRepository.findByUserIdWithoutLock(userId).orElseThrow(() -> new CustomException(ErrorType.RESOURCE_NOT_FOUND, "검색한 USER ID: "+ userId));
    }
}
