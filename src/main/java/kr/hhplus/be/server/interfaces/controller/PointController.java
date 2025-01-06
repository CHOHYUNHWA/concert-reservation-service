package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.interfaces.dto.ChargePointRequestDto;
import kr.hhplus.be.server.interfaces.dto.ChargePointResponseDto;
import kr.hhplus.be.server.interfaces.dto.GetPointResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class PointController {

    /**
     * 포인트 충전
     */
    @PatchMapping("/{userId}/point")
    public ResponseEntity<ChargePointResponseDto> chargePoint(
            @PathVariable("userId") Long userId,
            @RequestBody ChargePointRequestDto chargePointRequestDto
    ){
        return new ResponseEntity<>(new ChargePointResponseDto(1L, 100000L), HttpStatus.OK);
    }

    /**
     * 포인트 조회
     */
    @GetMapping("/{userId}/point")
    public ResponseEntity<GetPointResponseDto> getPoint(@PathVariable("userId") Long userId){
        return new ResponseEntity<>(new GetPointResponseDto(1L, 100000L), HttpStatus.OK);
    }
}
