package kr.hhplus.be.server.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class AvailableReservationConcertSeatResponse  {
    private Long concertId;
    private LocalDateTime concertTime;
    private Long totalSeats;
    private List<SeatDto> seats;
}
