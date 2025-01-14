package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.entity.Point;
import kr.hhplus.be.server.domain.service.PointService;
import kr.hhplus.be.server.domain.service.UserService;
import kr.hhplus.be.server.interfaces.dto.point.PointHttpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointFacade {

    private final PointService pointService;
    private final UserService userService;

    @Transactional
    public PointHttpDto.ChargePointResponseDto chargePoint(Long userId, Long chargeAmount) {
        userService.existsUser(userId);

        Point point = pointService.getPoint(userId);
        Point chargedPoint = pointService.chargePoint(userId, chargeAmount);

        return PointHttpDto.ChargePointResponseDto.of(chargedPoint);
    }

    public PointHttpDto.GetPointResponseDto getPoint(Long userId) {
        Point point = pointService.getPoint(userId);
        return PointHttpDto.GetPointResponseDto.of(point.getId(),point.getAmount());
    }


}
