package kr.hhplus.be.server.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "concert_schedule")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcertSchedule {

    @Id @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "AVAILABLE_RESERVATION_TIME", nullable = false)
    private LocalDateTime availableReservationTime;

    @Column(name = "CONCERT_TIME", nullable = false)
    private LocalDateTime concertTime;

    @Column(name = "CONCERT_ID", nullable = false)
    private Long concertId;

    public void checkStatus() {
        if(LocalDateTime.now().isBefore(this.availableReservationTime)){
            throw new CustomException(ErrorType.BEFORE_AVAILABLE_RESERVATION_AT, "예약 가능 시간 : " + this.availableReservationTime);
        }
        if(LocalDateTime.now().isAfter(this.concertTime)){
            throw new CustomException(ErrorType.ALREADY_CONCERT_START, "예약 마감 시간 : " + this.concertTime);
        }
    }
}
