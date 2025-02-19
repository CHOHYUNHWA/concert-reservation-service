package kr.hhplus.be.server.config.kafka;

import kr.hhplus.be.server.interfaces.kafka.KafkaProducerInterceptor;
import kr.hhplus.be.server.interfaces.kafka.KafkaProducerListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(
            DefaultKafkaProducerFactory<String, String> factory,
            KafkaProducerInterceptor interceptor,
            KafkaProducerListener listener){
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(factory);
        kafkaTemplate.setProducerInterceptor(interceptor);
        kafkaTemplate.setProducerListener(listener);
        return kafkaTemplate;
    }
}
