package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.interfaces.dto.ReservationRequest;
import kr.hhplus.be.server.interfaces.dto.ReservationCompletedResponse;
import kr.hhplus.be.server.interfaces.dto.ReservationCompletedSeatDto;
import kr.hhplus.be.server.support.type.ReservationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/concert")
public class ReservationController {


    /**
     * 콘서트 예약
     */
    @PostMapping("/reservation")
    public ResponseEntity<ReservationCompletedResponse> reserveConcert(
            @RequestHeader("Token") String token,
            @RequestBody ReservationRequest reservationRequest
    ){
        return new ResponseEntity<>(
                new ReservationCompletedResponse(
                        1L,
                        1L,
                        "콘서트",
                        LocalDateTime.now(),
                        new ReservationCompletedSeatDto(1L, 10000L),
                        100000L,
                        ReservationStatus.PAYMENT_WAITING
                ), HttpStatus.CREATED);
    }

}
