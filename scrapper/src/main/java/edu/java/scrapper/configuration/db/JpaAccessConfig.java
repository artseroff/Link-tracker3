package edu.java.scrapper.configuration.db;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.jpa.JpaTgChatRepository;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.impl.jpa.JpaLinkService;
import edu.java.scrapper.service.impl.jpa.JpaTgChatService;
import edu.java.scrapper.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.service.updater.LinkUpdaterService;
import edu.java.scrapper.service.updater.jpa.JpaLinkUpdaterService;
import edu.java.scrapper.service.updater.sender.LinkUpdatesSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfig {

    private final AbstractUpdatesFetcher headUpdatesFetcher;

    public JpaAccessConfig(@Qualifier("headUpdatesFetcher") AbstractUpdatesFetcher headUpdatesFetcher) {
        this.headUpdatesFetcher = headUpdatesFetcher;
    }

    @Bean
    public TgChatService tgChatService(
        JpaTgChatRepository chatRepository, JpaLinkRepository linkRepository
    ) {
        return new JpaTgChatService(chatRepository, linkService(chatRepository, linkRepository));
    }

    @Bean
    public LinkService linkService(
        JpaTgChatRepository chatRepository,
        JpaLinkRepository linkRepository
    ) {
        return new JpaLinkService(linkRepository, headUpdatesFetcher, chatRepository);
    }

    @Bean
    public LinkUpdaterService linkUpdaterService(
        JpaLinkRepository linkRepository,
        ApplicationConfig config,
        LinkUpdatesSender linkUpdatesSender
    ) {
        return new JpaLinkUpdaterService(
            linkRepository,
            config,
            linkUpdatesSender,
            headUpdatesFetcher
        );
    }
}
