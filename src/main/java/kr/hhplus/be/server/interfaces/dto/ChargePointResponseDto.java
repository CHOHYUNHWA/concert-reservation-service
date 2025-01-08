package kr.hhplus.be.server.interfaces.dto;

import kr.hhplus.be.server.domain.entity.Point;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChargePointResponseDto {
    private Long userId;
    private Long currentPoint;

    public static ChargePointResponseDto of(Point point){
        return ChargePointResponseDto.builder()
                .userId(point.getUserId())
                .currentPoint(point.getAmount())
                .build();

    }

}
