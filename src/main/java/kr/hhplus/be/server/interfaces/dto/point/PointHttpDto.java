package kr.hhplus.be.server.interfaces.dto.point;

import kr.hhplus.be.server.domain.entity.Point;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class PointHttpDto {

    @Getter
    @Builder
    public static class ChargePointRequestDto{
        private Long amount;
    }

    @Data
    @Builder
    public static class ChargePointResponseDto{
        private Long userId;
        private Long currentPoint;

        public static ChargePointResponseDto of(Point point){
            return ChargePointResponseDto.builder()
                    .userId(point.getUserId())
                    .currentPoint(point.getAmount())
                    .build();

        }
    }

    @Data
    @Builder
    public static class GetPointResponseDto{
        private Long userId;
        private Long currentAmount;

        public static GetPointResponseDto of(Long userId, Long currentAmount) {
            return GetPointResponseDto.builder()
                    .userId(userId)
                    .currentAmount(currentAmount)
                    .build();
        }
    }
}
