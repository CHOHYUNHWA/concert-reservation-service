package kr.hhplus.be.server.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableReservationConcertDateResponse {
    private Long concertId;
    private List<ScheduleDto> schedules;

    public static AvailableReservationConcertDateResponse of(Long concertId, List<ScheduleDto> schedules){
        return AvailableReservationConcertDateResponse.builder().concertId(concertId).schedules(schedules).build();
    }
}
