package kr.hhplus.be.server.interfaces.dto.concert;

import kr.hhplus.be.server.domain.entity.Concert;
import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.support.type.ConcertStatus;
import kr.hhplus.be.server.support.type.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ConcertHttpDto {

    @Data
    @Builder
    public static class AvailableReservationConcertDateResponse {
        private Long concertId;
        private List<ScheduleDto> schedules;

        public static AvailableReservationConcertDateResponse of(Long concertId, List<ScheduleDto> schedules){
            return AvailableReservationConcertDateResponse.builder().concertId(concertId).schedules(schedules).build();
        }
    }

    @Data
    @Builder
    public static class AvailableReservationConcertResponse {
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

    @Data
    @Builder
    public static class AvailableReservationConcertSeatResponse {
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

    @Data
    @Builder
    public static class ScheduleDto {
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

    @Data
    @Builder
    public static class SeatDto {
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


}
