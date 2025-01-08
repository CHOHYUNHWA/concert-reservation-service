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
@Table(name = "POINT")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point {

    @Id @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "AMOUNT", nullable = false)
    private Long amount;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;


    public Point charge(Long chargeAmount) {

        if(chargeAmount <= 0){
            throw new CustomException(ErrorCode.INVALID_AMOUNT);
        }
        this.amount += chargeAmount;
        this.updatedAt = LocalDateTime.now();

        return this;
    }

    public Point usePoint(Long useAmount) {
        if(useAmount <= 0 || useAmount > this.amount){
            throw new CustomException(ErrorCode.INVALID_AMOUNT);
        }
        this.amount -= useAmount;
        this.updatedAt = LocalDateTime.now();

        return this;
    }
}
