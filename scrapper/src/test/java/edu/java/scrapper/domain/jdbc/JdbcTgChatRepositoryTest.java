package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.domain.AbstractTgChatRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JdbcTgChatRepositoryTest extends AbstractTgChatRepositoryTest {

    @Autowired
    public JdbcTgChatRepositoryTest(JdbcTgChatRepository chatRepository) {
        super(chatRepository);
    }
}
