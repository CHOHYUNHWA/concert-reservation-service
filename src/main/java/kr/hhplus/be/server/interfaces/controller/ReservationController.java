package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.application.facade.ReservationFacade;
import kr.hhplus.be.server.interfaces.dto.reservation.ReservationHttpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationFacade reservationFacade;


    /**
     * 콘서트 예약
     */
    @PostMapping
    public ResponseEntity<ReservationHttpDto.ReservationCompletedResponse> reserveConcert(
            @RequestHeader("Token") String token,
            @RequestBody ReservationHttpDto.ReservationRequest reservationRequest
    ){
        ReservationHttpDto.ReservationCompletedResponse reservation = reservationFacade.reservationWithPessimisticLock(reservationRequest);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

}
