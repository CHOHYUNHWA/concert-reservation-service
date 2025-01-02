package kr.hhplus.be.server.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AvailableReservationConcertDateResponse {
    private Long concertId;
    private List<ScheduleDto> schedules;
}
