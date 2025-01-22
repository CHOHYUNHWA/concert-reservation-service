package kr.hhplus.be.server.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "CONCERT_SCHEDULE")
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
            throw new CustomException(ErrorCode.BEFORE_AVAILABLE_RESERVATION_AT);
        }
        if(LocalDateTime.now().isAfter(this.concertTime)){
            throw new CustomException(ErrorCode.ALREADY_CONCERT_START);
        }
    }
}
