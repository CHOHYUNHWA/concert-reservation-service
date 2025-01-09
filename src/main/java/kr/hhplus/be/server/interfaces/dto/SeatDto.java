package kr.hhplus.be.server.interfaces.dto;

import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.support.type.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatDto {
    private Long seatId;
    private Long seatNumber;
    private SeatStatus status;
    private Long seatPrice;


    public static SeatDto of(Seat seat) {
        return SeatDto.builder()
                .seatId(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .status(seat.getSeatStatus())
                .seatPrice(seat.getSeatPrice())
                .build();
    }
}
