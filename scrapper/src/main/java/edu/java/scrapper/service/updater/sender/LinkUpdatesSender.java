package edu.java.scrapper.service.updater.sender;

import edu.java.request.LinkUpdateRequest;

public interface LinkUpdatesSender {
    void sendUpdates(LinkUpdateRequest updateRequest);
    /*private final ApplicationConfig config;
    private final BotClient botClient;
    private final ScrapperQueueProducer queueProducer;

    public void sendUpdates(LinkUpdateRequest updateRequest) {
        if (config.useQueue()) {
            queueProducer.send(updateRequest);
        } else {
            botClient.updates(updateRequest);
        }
    }*/
}
