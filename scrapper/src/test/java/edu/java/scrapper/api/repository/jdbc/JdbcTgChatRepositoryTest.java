package edu.java.scrapper.api.repository.jdbc;

import edu.java.scrapper.api.repository.AbstractTgChatRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JdbcTgChatRepositoryTest extends AbstractTgChatRepositoryTest {

    @Autowired
    public JdbcTgChatRepositoryTest(JdbcTgChatRepository chatRepository) {
        super(chatRepository);
    }
}
