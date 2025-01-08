package kr.hhplus.be.server.infra.repository.jpa;

import kr.hhplus.be.server.domain.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
}
