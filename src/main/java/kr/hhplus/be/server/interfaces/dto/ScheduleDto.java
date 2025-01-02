package kr.hhplus.be.server.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ScheduleDto {
    private Long scheduleId;
    private LocalDateTime concertTime;
    private LocalDateTime availableReservationTime;
}
