package kr.hhplus.be.server.interfaces.dto;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private Long userId;
    private Long reservationId;
}
