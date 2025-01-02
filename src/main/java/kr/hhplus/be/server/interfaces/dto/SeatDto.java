package kr.hhplus.be.server.interfaces.dto;

import kr.hhplus.be.server.support.type.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatDto {
    private Long seatId;
    private Long seatNumber;
    private SeatStatus status;
    private Long seatPrice;
}
