package kr.hhplus.be.server.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.type.ConcertStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "concert")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Concert {

    @Id @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE" , nullable = false)
    private String title;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConcertStatus status;

    public boolean checkStatus(){
        return this.status.equals(ConcertStatus.OPEN);
    }

}
