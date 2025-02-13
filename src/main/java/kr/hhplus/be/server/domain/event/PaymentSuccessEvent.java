package kr.hhplus.be.server.domain.event;

import kr.hhplus.be.server.support.type.PaymentStatus;
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


}
