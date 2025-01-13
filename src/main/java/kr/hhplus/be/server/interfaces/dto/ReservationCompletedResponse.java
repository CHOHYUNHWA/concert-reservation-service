package kr.hhplus.be.server.interfaces.dto;

import kr.hhplus.be.server.domain.entity.Reservation;
import kr.hhplus.be.server.support.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationCompletedResponse {
    private Long reservationId;
    private Long concertId;
    private String title;
    private LocalDateTime concertTime;
    private ReservationCompletedSeatDto seat;
    private Long totalPrice;
    private ReservationStatus reservationStatus;

    public static ReservationCompletedResponse of(
            Long reservationId,
            Long concertId,
            String title,
            LocalDateTime concertTime,
            Long totalPrice,
            ReservationStatus reservationStatus,ReservationCompletedSeatDto seat) {
        return ReservationCompletedResponse.builder()
                .reservationId(reservationId)
                .concertId(concertId)
                .title(title)
                .concertTime(concertTime)
                .seat(seat)
                .totalPrice(totalPrice)
                .reservationStatus(reservationStatus)
                .build();
    }
}
