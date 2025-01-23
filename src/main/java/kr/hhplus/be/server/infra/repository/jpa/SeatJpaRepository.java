package kr.hhplus.be.server.infra.repository.jpa;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.support.type.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.id = ?1")
    Optional<Seat> findByIdWithPessimisticLock(Long seatId);

    @Query("select s from Seat s where s.id = ?1")
    Optional<Seat> findByIdWithoutLock(Long seatId);

    //테스트용
    Optional<Seat> findById(Long id);

    List<Seat> findAllByConcertScheduleIdAndSeatStatus(Long concertScheduleId, SeatStatus seatStatus);
}
