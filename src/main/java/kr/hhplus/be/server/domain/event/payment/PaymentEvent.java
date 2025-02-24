package kr.hhplus.be.server.domain.event.payment;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.hhplus.be.server.domain.dto.PaymentEventCommand;
import kr.hhplus.be.server.domain.event.outbox.OutboxEvent;

import kr.hhplus.be.server.support.type.OutboxStatus;
import kr.hhplus.be.server.support.type.PaymentStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentEvent extends OutboxEvent {

    public PaymentEvent(Long id, String topic, String key, String payload, String type, String status, LocalDateTime createdAt, String uuid) {
        super(id, topic, key, payload, type, status, createdAt, uuid);
    }

    public static PaymentEvent from(String topic, OutboxStatus outboxStatus, PaymentEventCommand command) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String payload = objectMapper.writeValueAsString(command);
        return new PaymentEvent(
                null,
                topic,
                String.valueOf(command.getUserId()),
                payload,
                PaymentStatus.COMPLETED.name(),
                outboxStatus.name(),
                LocalDateTime.now(),
                command.getUuid());
    }

    public static PaymentEvent mapToPaymentEvent(OutboxEvent outboxEvent) {
        return new PaymentEvent(
                outboxEvent.getId(),
                outboxEvent.getTopic(),
                outboxEvent.getKey(),
                outboxEvent.getPayload(),
                outboxEvent.getType(),
                outboxEvent.getStatus(),
                outboxEvent.getCreatedAt(),
                outboxEvent.getUuid()
        );
    }
}
