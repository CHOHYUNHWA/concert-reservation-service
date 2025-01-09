package kr.hhplus.be.server.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorCode;
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
    private ReservationStatus status;

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

    //예약 생성
    public static Reservation create(ConcertSchedule schedule, Long seatId, Long userId) {
        return Reservation.builder()
                .concertId(schedule.getConcertId())
                .concertScheduleId(schedule.getId())
                .seatId(seatId)
                .userId(userId)
                .status(ReservationStatus.PAYMENT_WAITING)
                .reservedAt(LocalDateTime.now())
                .build();
    }

    //유효성 검증
    public void validateReservation(Long userId){
        if(!this.status.equals(ReservationStatus.PAYMENT_WAITING)){
            throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);
        }
        if(this.reservedAt.isBefore(LocalDateTime.now().minusMinutes(5))){
            throw new CustomException(ErrorCode.PAYMENT_TIMEOUT);
        }

        if(!this.userId.equals(userId)){
            throw new CustomException(ErrorCode.PAYMENT_USER_MISMATCH);
        }
    }

    //상태 변경
    public Reservation changeCompletedStatus(){
        return Reservation.builder()
                .concertId(this.concertId)
                .concertScheduleId(this.concertScheduleId)
                .seatId(this.seatId)
                .userId(this.userId)
                .status(ReservationStatus.PAYMENT_COMPLETED)
                .reservedAt(LocalDateTime.now())
                .build();
    }

}
