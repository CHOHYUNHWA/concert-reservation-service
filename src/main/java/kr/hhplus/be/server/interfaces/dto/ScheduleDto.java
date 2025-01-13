package kr.hhplus.be.server.interfaces.dto;

import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private Long scheduleId;
    private LocalDateTime concertTime;
    private LocalDateTime availableReservationTime;

    public static ScheduleDto of(ConcertSchedule concertSchedule) {
        return ScheduleDto.builder()
                .scheduleId(concertSchedule.getId())
                .concertTime(concertSchedule.getConcertTime())
                .availableReservationTime(concertSchedule.getAvailableReservationTime())
                .build();
    }

}
