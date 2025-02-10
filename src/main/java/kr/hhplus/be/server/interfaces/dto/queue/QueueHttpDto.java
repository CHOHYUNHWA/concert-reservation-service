package kr.hhplus.be.server.interfaces.dto.queue;

import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.support.type.QueueStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

public class QueueHttpDto {

    @Data
    @Builder
    public static class QueueStatusResponseDto {
        private QueueStatus status;
        private Long remainingQueueCount;

        public static QueueStatusResponseDto of(QueueStatus status, Long remainingQueueCount) {
            return QueueStatusResponseDto.builder()
                    .status(status)
                    .remainingQueueCount(remainingQueueCount).build();
        }
    }


    @Getter
    @Builder
    public static class CreateTokenRequestDto{
        private Long userId;
    }

    @Data
    @Builder
    public static class CreatedTokenResponseDto {
        private String token;

        public static CreatedTokenResponseDto of(Queue queue) {
            return CreatedTokenResponseDto.builder()
                    .token(queue.getToken())
                    .build();
        }
    }
}
