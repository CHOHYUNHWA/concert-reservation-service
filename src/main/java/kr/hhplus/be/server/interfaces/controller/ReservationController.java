package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.application.facade.ReservationFacade;
import kr.hhplus.be.server.interfaces.dto.reservation.ReservationHttpDto;
import kr.hhplus.be.server.support.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concert")
public class ReservationController {

    private final ReservationFacade reservationFacade;


    /**
     * 콘서트 예약
     */
    @PostMapping("/reservation")
    public ResponseEntity<ReservationHttpDto.ReservationCompletedResponse> reserveConcert(
            @RequestHeader("Token") String token,
            @RequestBody ReservationHttpDto.ReservationRequest reservationRequest
    ){
        ReservationHttpDto.ReservationCompletedResponse reservation = reservationFacade.reservation(reservationRequest, token);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

}
