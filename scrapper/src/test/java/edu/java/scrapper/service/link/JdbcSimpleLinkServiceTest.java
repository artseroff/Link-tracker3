package edu.java.scrapper.service.link;

import edu.java.scrapper.configuration.db.AccessType;
import edu.java.scrapper.domain.LinkRepository;
import edu.java.scrapper.domain.SubscriptionRepository;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
public class JdbcSimpleLinkServiceTest extends AbstractSimpleLinkServiceTest {

    private final JdbcClient jdbcClient;

    @Autowired
    public JdbcSimpleLinkServiceTest(
        LinkService linkService,
        TgChatService chatService,
        LinkRepository linkRepository,
        SubscriptionRepository subscriptionRepository,
        JdbcClient jdbcClient
    ) {
        super(linkService, chatService, linkRepository, subscriptionRepository);
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void truncateTableLinks() {
        jdbcClient.sql("truncate table links restart identity CASCADE")
            .update();
    }

    @DynamicPropertySource
    static void setAccessType(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> AccessType.JDBC);
    }
}
