package edu.java.scrapper.service.queue;

import edu.java.request.LinkUpdateRequest;
import edu.java.scrapper.configuration.kafka.KafkaProducerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapperQueueProducer {
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;
    private final KafkaProducerConfig kafka;

    public void send(LinkUpdateRequest update) {
        try {
            kafkaTemplate.send(kafka.topic(), update);
        } catch (Exception ex) {
            log.error("Ошибка при отправке сообщения в очередь", ex);
        }
    }
}
