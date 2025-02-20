package kr.hhplus.be.server.interfaces.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.dto.PaymentEventCommand;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class PaymentMessageConsumer implements KafkaMessageConsumer{

    private final ObjectMapper objectMapper;
    private final AtomicReference<PaymentMessagePayload> lastReceivedMessage = new AtomicReference<>(); // ✅ 마지막 메시지 저장 변수

    @Override
    @KafkaListener(topics = "concert-payment", groupId = "concert")
    public void handle(Message<String> message, Acknowledgment acknowledgment) {
        log.info("Received headers: {}, payload: {}", message.getHeaders(), message.getPayload());
        try {
            PaymentMessagePayload paymentMessagePayload = objectMapper.readValue(message.getPayload(), PaymentMessagePayload.class);

            String paymentType = new String(message.getHeaders().get("payment-type", byte[].class), StandardCharsets.UTF_8);
            lastReceivedMessage.set(paymentMessagePayload); // ✅ 마지막으로 받은 메시지 저장


            if("COMPLETED".equals(paymentType)){
                log.info("✅ 결제 완료 메시지 처리: {}", paymentMessagePayload);

                //결제 완료 시
            } else if("CANCELLED".equals(paymentType)){
                log.info("❌ 결제 취소 메시지 처리: {}", paymentMessagePayload);

                //결제 취소 시
            }

        } catch (JsonProcessingException e) {
            log.error("Error processing Kafka message", e);
        }

    }

    // ✅ 마지막으로 받은 메시지 반환 메서드 추가
    public PaymentMessagePayload getLastReceivedMessage() {
        return lastReceivedMessage.get();
    }
}
