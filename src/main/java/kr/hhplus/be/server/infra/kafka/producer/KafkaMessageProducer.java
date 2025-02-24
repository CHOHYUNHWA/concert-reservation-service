package kr.hhplus.be.server.infra.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.event.payment.PaymentEvent;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void testSend(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // ✅ JSON 변환 후 전송
            String jsonMessage = objectMapper.writeValueAsString(Map.of("message", message));
            kafkaTemplate.send("test-topic", jsonMessage);
        } catch (Exception e) {
            log.error("Error serializing message to JSON", e);
            throw new RuntimeException("Error serializing message to JSON", e);
        }
    }

    public void send(PaymentEvent event){
        try{
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(event.getTopic(), event.getKey(), event.getPayload());
            producerRecord.headers().add("payment-type", event.getType().getBytes(StandardCharsets.UTF_8));
            log.info("Payment message sent to Kafka topic: {}", event.getTopic());
            kafkaTemplate.send(producerRecord);
        } catch (Exception e){
            log.error("Error publishing event to Kafka", e);
            throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR, "Error publishing event to Kafka");
        }
    }
}
