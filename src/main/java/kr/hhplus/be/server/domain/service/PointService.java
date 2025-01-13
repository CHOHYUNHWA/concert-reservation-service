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
    public Point getPoint(Long userId) {
        return pointRepository.findPointWithLock(userId);
    }

    //포인트 사용
    public Point usePoint(Long userId, Long useAmount) {

        Point point = pointRepository.findPointWithLock(userId);
        Point usedPoint = point.usePoint(useAmount);

        return pointRepository.save(usedPoint);
    }

    //포인트 충전
    public Point chargePoint(Long userId, Long chargeAmount) {

        Point point = pointRepository.findPointWithLock(userId);
        Point chargedPoint = point.charge(chargeAmount);

        return pointRepository.save(chargedPoint);
    }

}
