package kr.hhplus.be.server.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequest {
    private Long userId;
    private Long concertId;
    private Long concertScheduleId;
    private Long seatId;
}
