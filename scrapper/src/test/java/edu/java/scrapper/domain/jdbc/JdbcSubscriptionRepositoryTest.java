package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.domain.AbstractSubscriptionRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest
public class JdbcSubscriptionRepositoryTest extends AbstractSubscriptionRepositoryTest {

    private final JdbcClient jdbcClient;

    @Autowired
    public JdbcSubscriptionRepositoryTest(
        JdbcClient jdbcClient
    ) {
        super(
            new JdbcSubscriptionRepository(jdbcClient),
            new JdbcLinkRepository(jdbcClient),
            new JdbcTgChatRepository(jdbcClient)
        );
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void truncateTableLinks() {
        jdbcClient.sql("truncate table links restart identity CASCADE")
            .update();
    }
}
