package edu.java.scrapper.configuration.db;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.LinkRepository;
import edu.java.scrapper.domain.SubscriptionRepository;
import edu.java.scrapper.domain.TgChatRepository;
import edu.java.scrapper.domain.jooq.repository.JooqLinkRepository;
import edu.java.scrapper.domain.jooq.repository.JooqSubscriptionRepository;
import edu.java.scrapper.domain.jooq.repository.JooqTgChatRepository;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.impl.SimpleLinkService;
import edu.java.scrapper.service.impl.SimpleTgChatService;
import edu.java.scrapper.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.service.updater.LinkUpdaterService;
import edu.java.scrapper.service.updater.SimpleLinkUpdaterService;
import edu.java.scrapper.service.updater.sender.SendService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {

    private final DSLContext dslContext;
    private final AbstractUpdatesFetcher headUpdatesFetcher;

    @Autowired
    public JooqAccessConfiguration(
        DSLContext dslContext,
        @Qualifier("headUpdatesFetcher") AbstractUpdatesFetcher headUpdatesFetcher
    ) {
        this.dslContext = dslContext;
        this.headUpdatesFetcher = headUpdatesFetcher;
    }

    @Bean
    public TgChatRepository tgChatRepository() {
        return new JooqTgChatRepository(dslContext);
    }

    @Bean
    public LinkRepository linkRepository() {
        return new JooqLinkRepository(dslContext);
    }

    @Bean
    public SubscriptionRepository subscriptionRepository() {
        return new JooqSubscriptionRepository(dslContext);
    }

    @Bean
    public TgChatService tgChatService() {
        return new SimpleTgChatService(tgChatRepository(), linkService());
    }

    @Bean
    public LinkService linkService() {
        return new SimpleLinkService(
            tgChatRepository(),
            linkRepository(),
            subscriptionRepository(),
            headUpdatesFetcher
        );
    }

    @Bean
    public LinkUpdaterService linkUpdaterService(ApplicationConfig config, SendService sendService) {
        return new SimpleLinkUpdaterService(
            linkRepository(),
            subscriptionRepository(),
            config,
            sendService,
            headUpdatesFetcher
        );
    }
}
