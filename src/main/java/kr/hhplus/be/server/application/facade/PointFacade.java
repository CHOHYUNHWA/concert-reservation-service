package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.entity.Point;
import kr.hhplus.be.server.domain.service.PointService;
import kr.hhplus.be.server.domain.service.UserService;
import kr.hhplus.be.server.interfaces.dto.ChargePointResponseDto;
import kr.hhplus.be.server.interfaces.dto.GetPointResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointFacade {

    private final PointService pointService;
    private final UserService userService;

    public ChargePointResponseDto chargePoint(Long userId, Long chargeAmount) {
        userService.existsUser(userId);

        Point point = pointService.getPoint(userId);
        Point chargedPoint = pointService.chargePoint(userId, chargeAmount);

        return ChargePointResponseDto.of(chargedPoint);
    }

    public GetPointResponseDto getPoint(Long userId) {
        Point point = pointService.getPoint(userId);
        return GetPointResponseDto.of(point.getId(),point.getAmount());
    }


}
