package edu.java.scrapper.api.repository.jdbc;

import edu.java.scrapper.api.repository.AbstractSubscriptionRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest
public class JdbcSubscriptionRepositoryTest extends AbstractSubscriptionRepositoryTest {

    private final JdbcClient jdbcClient;

    @Autowired
    public JdbcSubscriptionRepositoryTest(
        JdbcSubscriptionRepository subscriptionRepository,
        JdbcLinkRepository linkRepository,
        JdbcTgChatRepository chatRepository,
        JdbcClient jdbcClient
    ) {
        super(subscriptionRepository, linkRepository, chatRepository);
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void truncateTableLinks() {
        jdbcClient.sql("truncate table links restart identity CASCADE")
            .update();
    }
}
