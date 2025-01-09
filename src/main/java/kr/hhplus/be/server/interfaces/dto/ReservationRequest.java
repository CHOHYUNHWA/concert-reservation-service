package kr.hhplus.be.server.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class ReservationRequest {
    private Long userId;
    private Long concertId;
    private Long concertScheduleId;
    private Long seatId;
}
