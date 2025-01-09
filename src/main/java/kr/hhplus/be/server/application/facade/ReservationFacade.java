package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.entity.Concert;
import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.entity.Reservation;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.domain.service.ConcertService;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.domain.service.ReservationService;
import kr.hhplus.be.server.interfaces.dto.ReservationCompletedResponse;
import kr.hhplus.be.server.interfaces.dto.ReservationCompletedSeatDto;
import kr.hhplus.be.server.interfaces.dto.ReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ReservationFacade {

    private final QueueService queueService;
    private final ConcertService concertService;
    private final ReservationService reservationService;

    public ReservationCompletedResponse reservation(ReservationRequest reservationRequest, String token) {
        //토큰 검증
        queueService.validateToken(token);

        //콘서트 조회
        Concert concert = concertService.getConcert(reservationRequest.getConcertId());

        //콘서트 스케쥴 조회
        ConcertSchedule concertScheduleInfo = concertService.getConcertScheduleInfo(reservationRequest.getConcertScheduleId());

        //좌석 조회
        Seat findSeat = concertService.getSeat(reservationRequest.getSeatId());

        //콘서트,좌석 유효성 검증
        concertService.isAvailableReservationSeat(concertScheduleInfo, findSeat);

        //예약 생성
        Reservation reservation = reservationService.createReservation(concertScheduleInfo, findSeat, findSeat.getId());

        //좌석 상태 변경
        concertService.assignSeat(findSeat);


        ReservationCompletedSeatDto seat = ReservationCompletedSeatDto.of(findSeat.getSeatNumber(), findSeat.getSeatPrice());
        return ReservationCompletedResponse.of(
                reservation.getId(),
                concert.getId(),
                concert.getTitle(),
                concertScheduleInfo.getConcertTime(),
                seat.getSeatPrice(),
                reservation.getStatus(),
                seat);
    }

}
