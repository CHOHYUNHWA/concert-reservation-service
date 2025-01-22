package kr.hhplus.be.server.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import kr.hhplus.be.server.support.type.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "queue")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Queue {

    @Id @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TOKEN", nullable = false)
    private String token;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private QueueStatus status;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ENTERED_AT")
    private LocalDateTime enteredAt;

    @Column(name = "EXPIRED_AT")
    private LocalDateTime expiredAt;


    public static Queue createToken(Long activeCount){
        String token = UUID.randomUUID().toString();

        return Queue.builder()
                .token(token)
                .status((activeCount < 30) ? QueueStatus.ACTIVE : QueueStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .enteredAt((activeCount < 30) ? LocalDateTime.now() : null)
                .expiredAt((activeCount < 30) ? LocalDateTime.now().plusMinutes(10) : null)
                .build();
    }

    public void activate(){
        this.status = QueueStatus.ACTIVE;
    }

    public void expiredToken(){
        this.status = QueueStatus.EXPIRED;
    }

    public boolean checkStatus() {
        if(this.status.equals(QueueStatus.EXPIRED)) {
            throw new CustomException(ErrorType.INVALID_TOKEN, "현재 토큰 상태: " +this.status);
        }
        return this.getStatus() == QueueStatus.ACTIVE;
    }

    public void validateToken(){
        if(this.expiredAt == null || this.expiredAt.isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorType.INVALID_TOKEN, "현재 토큰 상태: " +this.status);
        }
    }

}