package kr.hhplus.be.server.interfaces.dto;

import kr.hhplus.be.server.support.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ReservationCompletedResponse {
    private Long reservationId;
    private Long concertId;
    private String title;
    private LocalDateTime concertTime;
    private List<ReservationCompletedSeatDto> seats;
    private Long totalPrice;
    private ReservationStatus reservationStatus;
}
