package kr.hhplus.be.server.interfaces.dto;

import kr.hhplus.be.server.domain.entity.Concert;
import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.support.type.ConcertStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableReservationConcertResponse {
    private Long id;
    private String title;
    private String description;
    private ConcertStatus status;

    public static AvailableReservationConcertResponse of(Concert concert) {
        return AvailableReservationConcertResponse.builder()
                .id(concert.getId())
                .title(concert.getTitle())
                .description(concert.getDescription())
                .status(concert.getStatus())
                .build();
    }
}
