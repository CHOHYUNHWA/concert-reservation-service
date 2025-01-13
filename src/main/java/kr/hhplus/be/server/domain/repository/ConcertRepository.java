package kr.hhplus.be.server.domain.repository;

import kr.hhplus.be.server.domain.entity.Concert;
import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.entity.Reservation;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.support.type.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertRepository {

    List<Concert> findConcertsAll();

    List<ConcertSchedule> findConcertSchedulesAllByConcertId(Long concertId);

    List<Seat> findSeatsAllByConcertScheduleId(Long concertScheduleId);

    Concert findConcertByConcertId(Long concertId);

    ConcertSchedule findConcertScheduleByConcertScheduleId(Long concertScheduleId);

    Seat saveSeat(Seat assignedSeat);

    Seat findSeatById(Long seatId);

    List<Reservation> findExpiredReservation(ReservationStatus reservationStatus, LocalDateTime localDateTime);
}
