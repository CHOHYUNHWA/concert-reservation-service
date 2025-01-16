package kr.hhplus.be.server.interfaces.dto.payment;

import kr.hhplus.be.server.support.type.PaymentStatus;
import lombok.*;

public class PaymentHttpDto {

    @Getter
    @Builder
    public static class PaymentRequestDto {
        private Long userId;
        private Long reservationId;
    }

    @Data
    @Builder
    public static class PaymentCompletedResponse {
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
}
