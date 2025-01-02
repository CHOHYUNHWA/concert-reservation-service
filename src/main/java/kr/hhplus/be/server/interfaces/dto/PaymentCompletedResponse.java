package kr.hhplus.be.server.interfaces.dto;

import kr.hhplus.be.server.support.type.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentCompletedResponse {
    private Long paymentId;
    private Long amount;
    private PaymentStatus paymentStatus;
}
