package edu.java.bot.kafka;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class KafkaIntegrationTest {

    static KafkaContainer KAFKA;

    static {
        KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.4"));
        KAFKA.start();
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("kafka.scrapper-topic", () -> "bot-test.scrapper-topic");
        registry.add("kafka.consumer-group", () -> "bot-test-group");
        registry.add("kafka.dlq-topic", () -> "bot-test.scrapper-topic_dlq");
    }

}
