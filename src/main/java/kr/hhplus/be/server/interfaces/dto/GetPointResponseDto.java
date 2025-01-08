package kr.hhplus.be.server.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetPointResponseDto {
    private Long userId;
    private Long currentAmount;

    public static GetPointResponseDto of(Long userId, Long currentAmount) {
        return GetPointResponseDto.builder()
                .userId(userId)
                .currentAmount(currentAmount)
                .build();
    }
}
