package edu.java.scrapper.domain.jooq;

import edu.java.scrapper.configuration.db.AccessType;
import edu.java.scrapper.domain.AbstractTgChatRepositoryTest;
import edu.java.scrapper.domain.jooq.repository.JooqTgChatRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class JooqTgChatRepositoryTest extends AbstractTgChatRepositoryTest implements JooqTest {

    private final DSLContext dslContext;

    @Autowired
    public JooqTgChatRepositoryTest(DSLContext dslContext) {
        super(new JooqTgChatRepository(dslContext));
        this.dslContext = dslContext;
    }

    @Override
    public void truncateTableChats() {
        dslContext.truncate(Tables.CHATS).restartIdentity().cascade().execute();
    }
}
