package kr.hhplus.be.server.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import kr.hhplus.be.server.support.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
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

    @Version
    private Long version;

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
        if(this.status.equals(ReservationStatus.PAYMENT_COMPLETED)){
            throw new CustomException(ErrorType.ALREADY_PAID, "현재 좌석 상태: " + this.status);
        }
        if(this.reservedAt.isBefore(LocalDateTime.now().minusMinutes(5))){
            throw new CustomException(ErrorType.PAYMENT_TIMEOUT, "최초 예약 시간: " + this.reservedAt + " 현재 시간: " + LocalDateTime.now() );
        }

        if(!this.userId.equals(userId)){
            throw new CustomException(ErrorType.PAYMENT_USER_MISMATCH, "예약 USER ID: " + this.userId + " 결제 USER ID: " + userId);
        }
    }

    //상태 변경
    public void changeCompletedStatus(){
        this.status = ReservationStatus.PAYMENT_COMPLETED;
    }

    public void changeExpiredStatus() {
        this.status = ReservationStatus.EXPIRED;
    }
}
