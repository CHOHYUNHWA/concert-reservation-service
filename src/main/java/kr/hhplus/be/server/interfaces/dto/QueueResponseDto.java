package kr.hhplus.be.server.interfaces.dto;

import kr.hhplus.be.server.support.type.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class QueueResponseDto {
    private QueueStatus status;
    private Long remainingQueueCount;
}
