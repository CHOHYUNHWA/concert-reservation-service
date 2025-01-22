package kr.hhplus.be.server.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import kr.hhplus.be.server.support.type.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "seat")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SEAT_NUMBER", nullable = false)
    private Long seatNumber;

    @Column(name = "SEAT_PRICE", nullable = false)
    private Long seatPrice;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;

    @Column(name = "RESERVED_AT")
    private LocalDateTime reservedAt;

    @Column(name = "CONCERT_SCHEDULE_ID", nullable = false)
    private Long concertScheduleId;

    public void checkStatus() {
        if(this.seatStatus.equals(SeatStatus.UNAVAILABLE)){
            throw new CustomException(ErrorType.ALREADY_RESERVED_SEAT, "현재 좌석 상태: " + this.seatStatus);
        }
    }

    public void assign(){

        this.seatStatus = SeatStatus.UNAVAILABLE;
        this.reservedAt = LocalDateTime.now();
    }

    public void toAvailable(){
        this.seatStatus = SeatStatus.AVAILABLE;
    }
}
