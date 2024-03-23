package edu.java.scrapper.domain.jooq;

import edu.java.scrapper.domain.AbstractSubscriptionRepositoryTest;
import edu.java.scrapper.domain.jooq.repository.JooqLinkRepository;
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
        DSLContext dslContext
    ) {
        super(
            new JooqSubscriptionRepository(dslContext),
            new JooqLinkRepository(dslContext),
            new JooqTgChatRepository(dslContext)
        );
        this.dslContext = dslContext;
    }

    @Override
    public void truncateTableLinks() {
        dslContext.truncate(Tables.LINKS).restartIdentity().cascade().execute();
    }
}
