package edu.java.scrapper.service.link;

import edu.java.scrapper.configuration.db.AccessType;
import edu.java.scrapper.domain.LinkRepository;
import edu.java.scrapper.domain.SubscriptionRepository;
import edu.java.scrapper.domain.jooq.Tables;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
public class JooqSimpleLinkServiceTest extends AbstractSimpleLinkServiceTest {

    private final DSLContext dslContext;

    @Autowired
    public JooqSimpleLinkServiceTest(
        LinkService linkService,
        TgChatService chatService,
        LinkRepository linkRepository,
        SubscriptionRepository subscriptionRepository,
        DSLContext dslContext
    ) {
        super(linkService, chatService, linkRepository, subscriptionRepository);
        this.dslContext = dslContext;
    }

    @Override
    public void truncateTableLinks() {
        dslContext.truncate(Tables.LINKS).restartIdentity().cascade().execute();
    }

    @DynamicPropertySource
    static void setAccessType(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> AccessType.JOOQ);
    }
}
