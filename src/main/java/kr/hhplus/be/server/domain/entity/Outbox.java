package kr.hhplus.be.server.domain.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.event.outbox.OutboxEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(name = "event_key", nullable = false)
    private String eventKey;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(nullable = false)
    private String uuid;

    public static Outbox from(OutboxEvent event) {
        return Outbox.builder()
                .id(event.getId())
                .topic(event.getTopic())
                .eventKey(event.getKey())
                .payload(event.getPayload())
                .type(event.getType())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .uuid(event.getUuid())
                .build();
    }

    public OutboxEvent of() {
        return OutboxEvent.builder()
                .id(this.id)
                .topic(this.topic)
                .key(this.eventKey)
                .payload(this.payload)
                .type(this.type)
                .status(this.type)
                .createdAt(this.createdAt)
                .uuid(this.uuid)
                .build();
    }
}
