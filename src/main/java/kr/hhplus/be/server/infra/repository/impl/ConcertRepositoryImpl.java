package kr.hhplus.be.server.infra.repository.impl;

import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.repository.ConcertRepository;
import kr.hhplus.be.server.infra.repository.jpa.ConcertJpaRepository;
import kr.hhplus.be.server.infra.repository.jpa.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infra.repository.jpa.SeatJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;
    private final SeatJpaRepository seatJpaRepository;

}
