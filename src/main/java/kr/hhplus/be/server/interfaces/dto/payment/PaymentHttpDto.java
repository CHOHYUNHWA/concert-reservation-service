package kr.hhplus.be.server.interfaces.dto.payment;

import kr.hhplus.be.server.support.type.PaymentStatus;
import lombok.*;

public class PaymentHttpDto {

    @Getter
    public static class PaymentRequestDto {
        private Long userId;
        private Long reservationId;

        @Builder
        public static PaymentRequestDto of(Long userId, Long reservationId) {
            return PaymentRequestDto.builder()
                    .userId(userId)
                    .reservationId(reservationId)
                    .build();
        }
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
