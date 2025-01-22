package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.application.facade.ConcertFacade;
import kr.hhplus.be.server.interfaces.dto.concert.*;
import kr.hhplus.be.server.support.type.ConcertStatus;
import kr.hhplus.be.server.support.type.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertFacade concertFacade;

    /**
     * 예약 가능 콘서트 리스트 조회
     */
    @GetMapping
    public ResponseEntity<List<ConcertHttpDto.AvailableReservationConcertResponse>> getAvailableReservationConcertSeats(
            @RequestHeader("Token") String token
    ) {
        List<ConcertHttpDto.AvailableReservationConcertResponse> availableReservationConcerts = concertFacade.getConcerts(token);
        return new ResponseEntity<>(availableReservationConcerts, HttpStatus.OK);
    }


    /**
     * 예약 가능 콘서트 날짜 조회
     */
    @GetMapping("/{concertId}/schedules")
    public ResponseEntity<ConcertHttpDto.AvailableReservationConcertDateResponse> getAvailableConcertDate(
            @RequestHeader("Token") String token,
            @PathVariable("concertId") Long concertId
    ) {
        ConcertHttpDto.AvailableReservationConcertDateResponse availableReservationConcertSchedules = concertFacade.getConcertSchedules(token, concertId);
        return new ResponseEntity<>(availableReservationConcertSchedules, HttpStatus.OK);
    }


    /**
     * 예약 가능 콘서트 좌석 조회
     */
    @GetMapping("/{concertId}/schedules/{concertScheduleId}/seats")
    public ResponseEntity<ConcertHttpDto.AvailableReservationConcertSeatResponse> getAvailableConcertSeats(
            @RequestHeader("Token") String token,
            @PathVariable("concertId") Long concertId,
            @PathVariable("concertScheduleId") Long concertScheduleId
    ) {
        ConcertHttpDto.AvailableReservationConcertSeatResponse availableReservationConcertSeats = concertFacade.getConcertSeats(token, concertId, concertScheduleId);
        return new ResponseEntity<>(availableReservationConcertSeats, HttpStatus.OK);
    }
}
