package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.interfaces.dto.AvailableReservationConcertDateResponse;
import kr.hhplus.be.server.interfaces.dto.AvailableReservationConcertSeatResponse;
import kr.hhplus.be.server.interfaces.dto.ScheduleDto;
import kr.hhplus.be.server.interfaces.dto.SeatDto;
import kr.hhplus.be.server.support.type.SeatStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/concert")
public class ConcertController {


    /**
     * 예약 가능 콘서트 날짜 조회
     */
    @GetMapping("/{concertId}/schedule")
    public ResponseEntity<AvailableReservationConcertDateResponse> getAvailableConcertDate(
            @RequestHeader("Token") String token,
            @PathVariable("concertId") String concertId
    ) {
        return new ResponseEntity<>(new AvailableReservationConcertDateResponse(
                1L,
                List.of(
                        new ScheduleDto(
                                1L,
                                LocalDateTime.now(),
                                LocalDateTime.now()))
        ), HttpStatus.OK);
    }


    /**
     * 예약 가능 콘서트 좌석 조회
     */
    @GetMapping("/{concertId}/schedule/{concertScheduleId}/seats")
    public ResponseEntity<AvailableReservationConcertSeatResponse> getAvailableConcertSeats(
            @RequestHeader("Token") String token,
            @PathVariable("concertId") Long concertId,
            @PathVariable("concertScheduleId") Long concertScheduleId
    ) {
        return new ResponseEntity<>(new AvailableReservationConcertSeatResponse(
                1L, LocalDateTime.now(), 50L,
                List.of(new SeatDto(1L, 1L, SeatStatus.AVAILABLE, 100000L),
                        new SeatDto(2L, 2L, SeatStatus.AVAILABLE, 100000L))
        ),HttpStatus.OK);
    }
}
