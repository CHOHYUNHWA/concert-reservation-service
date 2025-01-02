package kr.hhplus.be.server.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChargePointResponseDto {
    private Long userId;
    private Long currentPoint;
}
