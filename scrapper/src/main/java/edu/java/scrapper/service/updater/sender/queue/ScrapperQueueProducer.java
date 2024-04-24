package edu.java.scrapper.service.updater.sender.queue;

import edu.java.request.LinkUpdateRequest;
import edu.java.scrapper.service.updater.sender.LinkUpdatesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
@Slf4j
public class ScrapperQueueProducer implements LinkUpdatesSender {
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;
    private final String topic;

    @Override
    public void sendUpdates(LinkUpdateRequest updateRequest) {
        try {
            kafkaTemplate.send(topic, updateRequest);
        } catch (Exception ex) {
            log.error("Ошибка при отправке сообщения в очередь", ex);
        }
    }
}
