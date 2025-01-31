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
@Table(name = "point")
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

    @Version
    private Long version;


    public void charge(Long chargeAmount) {

        if(chargeAmount <= 0){
            throw new CustomException(ErrorType.INVALID_AMOUNT, "현재 잔액: " + this.amount + " 충전 요청 금액: " + chargeAmount);
        }
        this.amount += chargeAmount;
        this.updatedAt = LocalDateTime.now();
    }

    public void usePoint(Long useAmount) {
        if(useAmount <= 0 || useAmount > this.amount){
            throw new CustomException(ErrorType.INVALID_AMOUNT, "현재 잔액: " + this.amount + " 결제 요청 금액: " + useAmount);
        }
        this.amount -= useAmount;
        this.updatedAt = LocalDateTime.now();
    }
}
