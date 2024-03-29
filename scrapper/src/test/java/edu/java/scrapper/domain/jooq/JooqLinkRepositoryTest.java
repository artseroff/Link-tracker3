package edu.java.scrapper.domain.jooq;

import edu.java.scrapper.domain.AbstractLinkRepositoryTest;
import edu.java.scrapper.domain.jooq.repository.JooqLinkRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class JooqLinkRepositoryTest extends AbstractLinkRepositoryTest {

    private final DSLContext dslContext;

    @Autowired
    public JooqLinkRepositoryTest(JooqLinkRepository linkRepository, DSLContext dslContext) {
        super(linkRepository);
        this.dslContext = dslContext;
    }

    @Override
    public void truncateTableLinks() {
        dslContext.truncate(Tables.LINKS).restartIdentity().cascade().execute();
    }
}
