package edu.java.scrapper.service.updater.sender;

import edu.java.request.LinkUpdateRequest;
import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.service.queue.ScrapperQueueProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendService {
    private final ApplicationConfig config;
    private final BotClient botClient;
    private final ScrapperQueueProducer queueProducer;

    public void sendUpdates(LinkUpdateRequest updateRequest) {
        if (config.useQueue()) {
            queueProducer.send(updateRequest);
        } else {
            botClient.updates(updateRequest);
        }
    }
}
