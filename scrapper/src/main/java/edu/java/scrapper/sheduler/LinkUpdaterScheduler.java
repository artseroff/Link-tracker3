package edu.java.scrapper.sheduler;

import edu.java.general.ApiException;
import edu.java.scrapper.service.updater.LinkUpdaterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@Slf4j
@Component
@ConditionalOnProperty(value = "app.scheduler.enable")
public class LinkUpdaterScheduler {

    private final LinkUpdaterService linkUpdaterService;

    public LinkUpdaterScheduler(LinkUpdaterService linkUpdaterService) {
        this.linkUpdaterService = linkUpdaterService;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.interval}")
    public void update() {
        try {
            int countUpdates = linkUpdaterService.update();
            log.info("LinkUpdaterScheduler обновил {} ссылок", countUpdates);
        } catch (ApiException | WebClientRequestException e) {
            log.error(e.toString());
        }

    }
}
