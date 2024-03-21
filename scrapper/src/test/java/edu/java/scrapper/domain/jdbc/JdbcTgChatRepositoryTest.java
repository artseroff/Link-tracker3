package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.domain.AbstractTgChatRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class JdbcTgChatRepositoryTest extends AbstractTgChatRepositoryTest {

    private final JdbcClient jdbcClient;

    @Autowired
    public JdbcTgChatRepositoryTest(JdbcTgChatRepository chatRepository, JdbcClient jdbcClient) {
        super(chatRepository);
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void truncateTableChats() {
        jdbcClient.sql("truncate table chats restart identity cascade")
            .update();
    }
}
