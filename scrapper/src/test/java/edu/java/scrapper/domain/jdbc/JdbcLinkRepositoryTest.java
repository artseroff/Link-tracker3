package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.domain.AbstractLinkRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class JdbcLinkRepositoryTest extends AbstractLinkRepositoryTest {

    private final JdbcClient jdbcClient;

    @Autowired
    public JdbcLinkRepositoryTest(JdbcClient jdbcClient) {
        super(new JdbcLinkRepository(jdbcClient));
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void truncateTableLinks() {
        jdbcClient.sql("truncate table links restart identity CASCADE")
            .update();
    }
}
