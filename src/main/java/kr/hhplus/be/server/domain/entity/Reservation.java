package kr.hhplus.be.server.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "RESERVATION")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @Column(name = "RESERVED_AT", nullable = false)
    private LocalDateTime reservedAt;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "CONCERT_ID", nullable = false)
    private Long concertId;

    @Column(name = "CONCERT_SCHEDULE_ID", nullable = false)
    private Long concertScheduleId;

    @Column(name = "SEAT_ID", nullable = false)
    private Long seatId;

}
