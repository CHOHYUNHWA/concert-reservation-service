package kr.hhplus.be.server.application.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.entity.Concert;
import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.entity.Reservation;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.domain.service.ConcertService;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.domain.service.ReservationService;
import kr.hhplus.be.server.interfaces.dto.reservation.ReservationHttpDto;
import kr.hhplus.be.server.support.aop.RedisDistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationFacade {

    private final QueueService queueService;
    private final ConcertService concertService;
    private final ReservationService reservationService;

    @Transactional
    public ReservationHttpDto.ReservationCompletedResponse reservationWithPessimisticLock(ReservationHttpDto.ReservationRequest reservationRequest) {

        //콘서트 조회
        Concert concert = concertService.getConcert(reservationRequest.getConcertId());

        //콘서트 스케쥴 조회
        ConcertSchedule concertScheduleInfo = concertService.getConcertScheduleInfo(reservationRequest.getConcertScheduleId());

        //좌석 조회
        Seat findSeat = concertService.getSeatWithPessimisticLock(reservationRequest.getSeatId());

        //콘서트,좌석 유효성 검증
        concertService.isAvailableReservationSeat(concertScheduleInfo, findSeat);

        //예약 생성
        Reservation reservation = reservationService.createReservation(concertScheduleInfo, findSeat, findSeat.getId());

        //좌석 상태 변경
        concertService.assignSeat(findSeat);


        ReservationHttpDto.ReservationCompletedSeatDto seat = ReservationHttpDto.ReservationCompletedSeatDto.of(findSeat.getSeatNumber(), findSeat.getSeatPrice());
        return ReservationHttpDto.ReservationCompletedResponse.of(
                reservation.getId(),
                concert.getId(),
                concert.getTitle(),
                concertScheduleInfo.getConcertTime(),
                seat.getSeatPrice(),
                reservation.getStatus(),
                seat
        );
    }

    @Transactional
    public ReservationHttpDto.ReservationCompletedResponse reservationWithOptimisticLock(ReservationHttpDto.ReservationRequest reservationRequest) {

        //콘서트 조회
        Concert concert = concertService.getConcert(reservationRequest.getConcertId());

        //콘서트 스케쥴 조회
        ConcertSchedule concertScheduleInfo = concertService.getConcertScheduleInfo(reservationRequest.getConcertScheduleId());

        //좌석 조회
        Seat findSeat = concertService.getSeatWithOptimisticLock(reservationRequest.getSeatId());

        //콘서트,좌석 유효성 검증
        concertService.isAvailableReservationSeat(concertScheduleInfo, findSeat);

        //예약 생성
        Reservation reservation = reservationService.createReservation(concertScheduleInfo, findSeat, findSeat.getId());

        //좌석 상태 변경
        concertService.assignSeat(findSeat);


        ReservationHttpDto.ReservationCompletedSeatDto seat = ReservationHttpDto.ReservationCompletedSeatDto.of(findSeat.getSeatNumber(), findSeat.getSeatPrice());
        return ReservationHttpDto.ReservationCompletedResponse.of(
                reservation.getId(),
                concert.getId(),
                concert.getTitle(),
                concertScheduleInfo.getConcertTime(),
                seat.getSeatPrice(),
                reservation.getStatus(),
                seat
        );
    }


    @RedisDistributedLock(key = "'reservation:' + #reservationRequest.seatId")
    @Transactional
    public ReservationHttpDto.ReservationCompletedResponse reservationWithDistributedLock(ReservationHttpDto.ReservationRequest reservationRequest) {

        //콘서트 조회
        Concert concert = concertService.getConcert(reservationRequest.getConcertId());

        //콘서트 스케쥴 조회
        ConcertSchedule concertScheduleInfo = concertService.getConcertScheduleInfo(reservationRequest.getConcertScheduleId());

        //좌석 조회
        Seat findSeat = concertService.getSeatWithPessimisticLock(reservationRequest.getSeatId());

        log.info("Seat Status ={}", findSeat.getSeatStatus());

        //콘서트,좌석 유효성 검증
        concertService.isAvailableReservationSeat(concertScheduleInfo, findSeat);

        //예약 생성
        Reservation reservation = reservationService.createReservation(concertScheduleInfo, findSeat, findSeat.getId());

        //좌석 상태 변경
        concertService.assignSeat(findSeat);


        ReservationHttpDto.ReservationCompletedSeatDto seat = ReservationHttpDto.ReservationCompletedSeatDto.of(findSeat.getSeatNumber(), findSeat.getSeatPrice());
        return ReservationHttpDto.ReservationCompletedResponse.of(
                reservation.getId(),
                concert.getId(),
                concert.getTitle(),
                concertScheduleInfo.getConcertTime(),
                seat.getSeatPrice(),
                reservation.getStatus(),
                seat
        );
    }

}
