package kr.hhplus.be.server.domain.event.payment;

import kr.hhplus.be.server.domain.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessEvent {
    private Long reservationId;
    private Long amount;


    public static PaymentSuccessEvent of(Payment payment){
        return PaymentSuccessEvent.builder()
                .reservationId(payment.getReservationId())
                .amount(payment.getAmount())
                .build();
    }
}
