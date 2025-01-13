package kr.hhplus.be.server.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableReservationConcertSeatResponse  {
    private Long concertId;
    private LocalDateTime concertTime;
    private Long totalSeats;
    private List<SeatDto> seats;

    public static AvailableReservationConcertSeatResponse of(Long concertId, LocalDateTime concertTime, List<SeatDto> seats) {
        return AvailableReservationConcertSeatResponse.builder()
                .concertId(concertId)
                .concertTime(concertTime)
                .seats(seats)
                .build();
    }
}
