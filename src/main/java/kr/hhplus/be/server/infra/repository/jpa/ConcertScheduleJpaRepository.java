package kr.hhplus.be.server.infra.repository.jpa;

import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertSchedule, Long> {
}
