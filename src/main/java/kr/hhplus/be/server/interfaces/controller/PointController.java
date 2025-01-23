package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.application.facade.PointFacade;
import kr.hhplus.be.server.interfaces.dto.point.PointHttpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PointController {

    private final PointFacade pointFacade;

    /**
     * 포인트 충전
     */
    @PatchMapping("/{userId}/point")
    public ResponseEntity<PointHttpDto.ChargePointResponseDto> chargePoint(
            @PathVariable("userId") Long userId,
            @RequestBody PointHttpDto.ChargePointRequestDto chargePointRequestDto
    ){
        PointHttpDto.ChargePointResponseDto chargePointResponseDto = pointFacade.chargePointWithPessimisticLock(userId, chargePointRequestDto.getAmount());
        return new ResponseEntity<>(chargePointResponseDto, HttpStatus.OK);
    }

    /**
     * 포인트 조회
     */
    @GetMapping("/{userId}/point")
    public ResponseEntity<PointHttpDto.GetPointResponseDto> getPoint(
            @PathVariable("userId") Long userId
    ){
        PointHttpDto.GetPointResponseDto getPointResponseDto = pointFacade.getPoint(userId);
        return new ResponseEntity<>(getPointResponseDto, HttpStatus.OK);
    }
}
