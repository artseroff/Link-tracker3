package edu.java.scrapper.configuration.db;

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

    @Autowired
    public JdbcAccessConfig(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
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
