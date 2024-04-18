package edu.java.scrapper.configuration.db;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.LinkRepository;
import edu.java.scrapper.domain.SubscriptionRepository;
import edu.java.scrapper.domain.TgChatRepository;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import edu.java.scrapper.domain.jdbc.JdbcSubscriptionRepository;
import edu.java.scrapper.domain.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.impl.SimpleLinkService;
import edu.java.scrapper.service.impl.SimpleTgChatService;
import edu.java.scrapper.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.service.updater.LinkUpdaterService;
import edu.java.scrapper.service.updater.SimpleLinkUpdaterService;
import edu.java.scrapper.service.updater.sender.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfig {

    private final JdbcClient jdbcClient;

    private final AbstractUpdatesFetcher headUpdatesFetcher;

    @Autowired
    public JdbcAccessConfig(
        JdbcClient jdbcClient,
        @Qualifier("headUpdatesFetcher") AbstractUpdatesFetcher headUpdatesFetcher
    ) {
        this.jdbcClient = jdbcClient;
        this.headUpdatesFetcher = headUpdatesFetcher;
    }

    @Bean
    public TgChatRepository tgChatRepository() {
        return new JdbcTgChatRepository(jdbcClient);
    }

    @Bean
    public LinkRepository linkRepository() {
        return new JdbcLinkRepository(jdbcClient);
    }

    @Bean
    public SubscriptionRepository subscriptionRepository() {
        return new JdbcSubscriptionRepository(jdbcClient);
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
