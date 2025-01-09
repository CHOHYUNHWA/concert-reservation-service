package kr.hhplus.be.server.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorCode;
import kr.hhplus.be.server.support.type.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "SEAT")
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
            throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);
        }
    }

    public Seat assign(){

        this.seatStatus = SeatStatus.UNAVAILABLE;
        this.reservedAt = LocalDateTime.now();
        return this;
    }
}
