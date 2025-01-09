package kr.hhplus.be.server.infra.repository.impl;

import kr.hhplus.be.server.domain.entity.Concert;
import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.domain.repository.ConcertRepository;
import kr.hhplus.be.server.infra.repository.jpa.ConcertJpaRepository;
import kr.hhplus.be.server.infra.repository.jpa.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infra.repository.jpa.SeatJpaRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorCode;
import kr.hhplus.be.server.support.type.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;
    private final SeatJpaRepository seatJpaRepository;

    @Override
    public List<Concert> findConcertsAll() {
        return concertJpaRepository.findAll();
    }

    @Override
    public List<ConcertSchedule> findConcertSchedulesAllByConcertId(Long concertId) {
        LocalDateTime now = LocalDateTime.now();
        return concertScheduleJpaRepository.findByConcertIdAndAvailableReservationTimeBeforeAndConcertTimeAfter(concertId, now, now);
    }

    @Override
    public List<Seat> findSeatsAllByConcertScheduleId(Long concertScheduleId) {
        SeatStatus availableStatus = SeatStatus.AVAILABLE;
        return seatJpaRepository.findAllByConcertScheduleIdAndSeatStatus(concertScheduleId, availableStatus);
    }

    @Override
    public Concert findConcertByConcertId(Long concertId) {
        return concertJpaRepository.findById(concertId).orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));
    }

    @Override
    public ConcertSchedule findConcertScheduleByConcertScheduleId(Long concertScheduleId) {
        return concertScheduleJpaRepository.findById(concertScheduleId).orElseThrow(() -> new CustomException(ErrorCode.CONCERT_SCHEDULE_NOT_FOUND));
    }

    @Override
    public void saveSeat(Seat assignedSeat) {
        seatJpaRepository.save(assignedSeat);
    }

    @Override
    public Seat findSeatById(Long seatId) {
        return seatJpaRepository.findById(seatId).orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
    }

}
