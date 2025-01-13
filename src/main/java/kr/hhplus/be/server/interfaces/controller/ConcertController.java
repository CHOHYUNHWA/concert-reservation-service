package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.interfaces.dto.*;
import kr.hhplus.be.server.support.type.ConcertStatus;
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
     * 예약 가능 콘서트 리스트 조회
     */
    @GetMapping
    public ResponseEntity<List<AvailableReservationConcertResponse>> getAvailableReservationConcertSeats(
            @RequestHeader("Token") String token
    ){
        return new ResponseEntity<>(
                List.of(
                        new AvailableReservationConcertResponse(1L,
                                "콘서트타이틀",
                                "콘서트설명",
                                ConcertStatus.OPEN)),
                HttpStatus.OK);
    }


    /**
     * 예약 가능 콘서트 날짜 조회
     */
    @GetMapping("/{concertId}/schedules")
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
