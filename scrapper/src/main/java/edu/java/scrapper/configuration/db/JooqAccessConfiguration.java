package edu.java.scrapper.configuration.db;

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

    @Autowired
    public JooqAccessConfiguration(DSLContext dslContext) {
        this.dslContext = dslContext;
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
    public TgChatService tgChatService(@Qualifier("headUpdatesFetcher") AbstractUpdatesFetcher headUpdatesFetcher) {
        return new SimpleTgChatService(tgChatRepository(), linkService(headUpdatesFetcher));
    }

    @Bean
    public LinkService linkService(AbstractUpdatesFetcher headUpdatesFetcher) {
        return new SimpleLinkService(
            tgChatRepository(),
            linkRepository(),
            subscriptionRepository(),
            headUpdatesFetcher
        );
    }
}
