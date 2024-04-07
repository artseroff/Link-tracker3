package edu.java.bot.service.kafka.dlq;

import edu.java.bot.configuration.kafka.KafkaConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeadLetterQueue {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaConfig config;

    public void send(String message) {
        kafkaTemplate.send(config.dlqTopic(), message);
    }
}
