package edu.java.scrapper.configuration.db;

import edu.java.scrapper.domain.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.jpa.JpaTgChatRepository;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.impl.jpa.JpaLinkService;
import edu.java.scrapper.service.impl.jpa.JpaTgChatService;
import edu.java.scrapper.service.updater.AbstractUpdatesFetcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfig {

    @Bean
    public TgChatService tgChatService(
        JpaTgChatRepository chatRepository, JpaLinkRepository linkRepository, @Qualifier("headUpdatesFetcher")
    AbstractUpdatesFetcher headUpdatesFetcher
    ) {
        return new JpaTgChatService(chatRepository, linkService(chatRepository, linkRepository, headUpdatesFetcher));
    }

    @Bean
    public LinkService linkService(
        JpaTgChatRepository chatRepository,
        JpaLinkRepository linkRepository,
        AbstractUpdatesFetcher headUpdatesFetcher
    ) {
        return new JpaLinkService(linkRepository, headUpdatesFetcher, chatRepository);
    }
}
