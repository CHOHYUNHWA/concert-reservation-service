package kr.hhplus.be.server.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.type.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "AMOUNT", nullable = false)
    private Long amount;

    @Column(name = "PAYMENT_AT", nullable = false)
    private LocalDateTime paymentAt;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "RESERVATION_ID" , nullable = false)
    private Long reservationId;

    @Column(name = "PAYMENT_STATUS", nullable = false)
    private PaymentStatus paymentStatus;

    public static Payment create(Long reservationId, Long userId, Long amount){
        return Payment.builder()
                .reservationId(reservationId)
                .userId(userId)
                .amount(amount)
                .paymentAt(LocalDateTime.now())
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();
    }

}
