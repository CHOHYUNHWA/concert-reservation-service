package kr.hhplus.be.server.domain.entity;

import jakarta.persistence.*;
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
}
