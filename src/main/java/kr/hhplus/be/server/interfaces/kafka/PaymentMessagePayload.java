package kr.hhplus.be.server.interfaces.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentMessagePayload {
    private Long id;
    private Long reservationId;
    private Long userId;
    private Long amount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime paymentAt;
}
