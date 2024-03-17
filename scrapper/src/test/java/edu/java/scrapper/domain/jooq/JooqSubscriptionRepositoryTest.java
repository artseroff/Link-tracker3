package edu.java.scrapper.domain.jooq;

import edu.java.scrapper.domain.AbstractSubscriptionRepositoryTest;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import edu.java.scrapper.domain.jooq.repository.JooqSubscriptionRepository;
import edu.java.scrapper.domain.jooq.repository.JooqTgChatRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JooqSubscriptionRepositoryTest extends AbstractSubscriptionRepositoryTest {

    private final DSLContext dslContext;

    @Autowired
    public JooqSubscriptionRepositoryTest(
        JooqSubscriptionRepository subscriptionRepository,
        JdbcLinkRepository linkRepository,
        JooqTgChatRepository chatRepository,
        DSLContext dslContext
    ) {
        super(subscriptionRepository, linkRepository, chatRepository);
        this.dslContext = dslContext;
    }

    @Override
    public void truncateTableLinks() {
        dslContext.truncate(Tables.LINKS).restartIdentity().cascade().execute();
    }
}
