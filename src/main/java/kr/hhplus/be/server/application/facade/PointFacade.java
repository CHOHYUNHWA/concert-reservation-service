package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.entity.Point;
import kr.hhplus.be.server.domain.service.PointService;
import kr.hhplus.be.server.domain.service.UserService;
import kr.hhplus.be.server.interfaces.dto.point.PointHttpDto;
import kr.hhplus.be.server.support.aop.RedisDistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PointFacade {

    private final PointService pointService;
    private final UserService userService;

    @Transactional
    public PointHttpDto.ChargePointResponseDto chargePointWithPessimisticLock(Long userId, Long chargeAmount) {
        userService.existsUser(userId);

        Point chargedPoint = pointService.chargePointWithPessimisticLock(userId, chargeAmount);

        return PointHttpDto.ChargePointResponseDto.of(chargedPoint);
    }

    public PointHttpDto.ChargePointResponseDto chargePointWithOptimisticLock(Long userId, Long chargeAmount) {

        while (true) {

            try {
                userService.existsUser(userId);

                Point chargedPoint = pointService.chargePointWithOptimisticLock(userId, chargeAmount);

                return PointHttpDto.ChargePointResponseDto.of(chargedPoint);
            } catch (Exception e) {
                log.error("낙관적락 충전 실패 재 시도 : {}", e.getMessage());
            }
        }
    }


    @RedisDistributedLock(key = "'chargePoint:' + #userId")
    @Transactional
    public PointHttpDto.ChargePointResponseDto chargePointWithDistributedLock(Long userId, Long chargeAmount) {
        userService.existsUser(userId);

        Point chargedPoint = pointService.chargePointWithPessimisticLock(userId, chargeAmount);
        log.info("userId Of Point ={}", chargedPoint.getUserId());

        return PointHttpDto.ChargePointResponseDto.of(chargedPoint);
    }

    public PointHttpDto.GetPointResponseDto getPoint(Long userId) {
        Point point = pointService.getPointWithoutLock(userId);
        return PointHttpDto.GetPointResponseDto.of(point.getId(),point.getAmount());
    }
}
