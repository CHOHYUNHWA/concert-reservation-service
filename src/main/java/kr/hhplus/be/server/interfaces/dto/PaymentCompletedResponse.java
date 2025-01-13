package kr.hhplus.be.server.interfaces.dto;

import kr.hhplus.be.server.support.type.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompletedResponse {
    private Long paymentId;
    private Long amount;
    private PaymentStatus paymentStatus;

    public static PaymentCompletedResponse of(Long paymentId, Long amount, PaymentStatus paymentStatus) {
        return PaymentCompletedResponse.builder()
                .paymentId(paymentId)
                .amount(amount)
                .paymentStatus(paymentStatus)
                .build();
    }
}
