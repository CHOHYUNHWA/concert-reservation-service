package kr.hhplus.be.server.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.hhplus.be.server.domain.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEventCommand {
    private Long id;
    private Long reservationId;
    private Long userId;
    private Long amount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime paymentAt;
    private String uuid;

    public static PaymentEventCommand from(Payment payment){
        return PaymentEventCommand.builder()
                .id(payment.getId())
                .reservationId(payment.getReservationId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentAt(payment.getPaymentAt())
                .uuid(UUID.randomUUID().toString())
                .build();
    }
}
