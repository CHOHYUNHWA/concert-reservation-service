package kr.hhplus.be.server.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TokenResponseDto {
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
}
