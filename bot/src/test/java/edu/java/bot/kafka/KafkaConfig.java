package edu.java.bot.kafka;

import edu.java.request.LinkUpdateRequest;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfig extends KafkaIntegrationTest {
    @Bean
    public NewTopic scrapperTopic(@Value("${kafka.scrapper-topic}") String scrapperTopicName) {
        return TopicBuilder.name(scrapperTopicName)
            .partitions(1)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic topicDlq(@Value("${kafka.dlq-topic}") String dlqTopic) {
        return TopicBuilder
            .name(dlqTopic)
            .partitions(1)
            .replicas(1)
            .build();
    }

    @Bean
    public KafkaTemplate<String, LinkUpdateRequest> linkUpdaterKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        )));
    }
}
