package kr.hhplus.be.server.interfaces.dto;

import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.support.type.ConcertStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvailableReservationConcertResponse {
    private Long id;
    private String title;
    private String description;
    private ConcertStatus status;
}
