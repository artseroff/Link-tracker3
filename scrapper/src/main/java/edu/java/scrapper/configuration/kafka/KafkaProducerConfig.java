package edu.java.scrapper.configuration.kafka;

import edu.java.request.LinkUpdateRequest;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.validation.annotation.Validated;

@EnableKafka
@Validated
@ConditionalOnProperty(prefix = "app", name = "use-queue")
@ConfigurationProperties(prefix = "kafka", ignoreUnknownFields = false)
public record KafkaProducerConfig(String bootstrapServers, String topic) {
    @Bean
    public KafkaTemplate<String, LinkUpdateRequest> linkUpdaterKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        )));
    }

    @Bean
    public NewTopic scrapperTopic() {
        return TopicBuilder.name(topic)
            .partitions(1)
            .replicas(1)
            .compact()
            .build();
    }
}
