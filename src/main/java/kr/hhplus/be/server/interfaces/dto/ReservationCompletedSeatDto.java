package kr.hhplus.be.server.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationCompletedSeatDto {
    private Long seatNumber;
    private Long seatPrice;
}
