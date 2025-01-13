package kr.hhplus.be.server.interfaces.dto.reservation;

import kr.hhplus.be.server.support.type.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;

public class ReservationHttpDto {

    @Getter
    @Builder
    public static class ReservationRequest {
        private Long userId;
        private Long concertId;
        private Long concertScheduleId;
        private Long seatId;
    }

    @Data
    @Builder
    public static class ReservationCompletedResponse {
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
                ReservationStatus reservationStatus, ReservationCompletedSeatDto seat) {
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


    @Data
    @Builder
    public static class ReservationCompletedSeatDto {
        private Long seatNumber;
        private Long seatPrice;

        public static ReservationCompletedSeatDto of(Long seatNumber, Long seatPrice) {
            return ReservationCompletedSeatDto.builder()
                    .seatNumber(seatNumber)
                    .seatPrice(seatPrice)
                    .build();
        }
    }

}
