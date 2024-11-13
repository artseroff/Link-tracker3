package edu.java.bot.service.kafka.dlq;

import edu.java.bot.configuration.kafka.KafkaConfig;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "kafka", name = "enable")
public class DeadLetterQueueProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaConfig config;
    private final Counter errorsCounter;

    public void send(String message) {
        kafkaTemplate.send(config.dlqTopic(), message);
        errorsCounter.increment();
    }
}
