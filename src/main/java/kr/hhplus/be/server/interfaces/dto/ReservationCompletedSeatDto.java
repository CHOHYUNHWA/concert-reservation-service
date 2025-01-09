package kr.hhplus.be.server.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationCompletedSeatDto {
    private Long seatNumber;
    private Long seatPrice;

    public static ReservationCompletedSeatDto of(Long seatNumber, Long seatPrice) {
        return ReservationCompletedSeatDto.builder()
                .seatNumber(seatNumber)
                .seatPrice(seatPrice)
                .build();
    }
}
