package edu.java.scrapper.domain.jooq;

import edu.java.scrapper.domain.AbstractLinkRepositoryTest;
import edu.java.scrapper.domain.jdbc.JdbcLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class JooqLinkRepositoryTest extends AbstractLinkRepositoryTest {

    private final JdbcClient jdbcClient;

    @Autowired
    public JooqLinkRepositoryTest(JdbcLinkRepository linkRepository, JdbcClient jdbcClient) {
        super(linkRepository);
        this.jdbcClient = jdbcClient;
    }

    @BeforeEach
    public void truncateTableLinks() {
        jdbcClient.sql("truncate table links restart identity CASCADE")
            .update();
    }
}
