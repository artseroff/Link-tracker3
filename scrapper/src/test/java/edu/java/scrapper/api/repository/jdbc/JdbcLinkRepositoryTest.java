package edu.java.scrapper.api.repository.jdbc;

import edu.java.scrapper.api.repository.AbstractLinkRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class JdbcLinkRepositoryTest extends AbstractLinkRepositoryTest {

    private final JdbcClient jdbcClient;

    @Autowired
    public JdbcLinkRepositoryTest(JdbcLinkRepository linkRepository, JdbcClient jdbcClient) {
        super(linkRepository);
        this.jdbcClient = jdbcClient;
    }

    @BeforeEach
    public void truncateTableLinks() {
        jdbcClient.sql("truncate table links restart identity CASCADE")
            .update();
    }
}
