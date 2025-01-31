package kr.hhplus.be.server.domain.service;

import kr.hhplus.be.server.domain.entity.Point;
import kr.hhplus.be.server.domain.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private final PointRepository pointRepository;

    //포인트 조회
    public Point getPointWithPessimisticLock(Long userId) {
        return pointRepository.findByUserIdWithPessimisticLock(userId);
    }

    //포인트 사용
    public Point usePoint(Long userId, Long useAmount) {

        Point point = pointRepository.findByUserIdWithPessimisticLock(userId);
        point.usePoint(useAmount);

        return pointRepository.save(point);
    }

    //포인트 충전
    public Point chargePointWithPessimisticLock(Long userId, Long chargeAmount) {

        Point point = pointRepository.findByUserIdWithPessimisticLock(userId);
        point.charge(chargeAmount);

        return pointRepository.save(point);
    }

    public Point getPointWithoutLock(Long userId) {
        return pointRepository.findByUserIdWithoutLock(userId);
    }

    public Point chargePointWithOptimisticLock(Long userId, Long chargeAmount) {
        Point point = pointRepository.findByUserIdWithOptimisticLock(userId);
        point.charge(chargeAmount);

        return pointRepository.save(point);
    }

    public Point chargePointWithoutLock(Long userId, Long chargeAmount) {
        Point point = pointRepository.findByUserIdWithoutLock(userId);
        point.charge(chargeAmount);

        return pointRepository.save(point);
    }
}
