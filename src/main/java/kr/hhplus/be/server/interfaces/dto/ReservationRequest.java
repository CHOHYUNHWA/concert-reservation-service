package kr.hhplus.be.server.interfaces.dto;

import lombok.Data;

@Data
public class ReservationRequest {
    private Long userId;
    private Long concertId;
    private Long concertScheduleId;
    private Long seatId;
}
