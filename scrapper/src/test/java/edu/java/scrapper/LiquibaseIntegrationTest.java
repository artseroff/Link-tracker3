package edu.java.scrapper;
import edu.java.scrapper.configuration.DBConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(DBConfig.class)
public class LiquibaseIntegrationTest extends IntegrationTest {

    private final JdbcClient jdbcClient;

    @Autowired
    public LiquibaseIntegrationTest(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Test
    public void addChatTest() {
        int id = 1;
        int chatId = 12345;
        jdbcClient.sql("INSERT INTO chats (chat_id) VALUES (?)")
            .param(chatId)
            .update();
        Long actualChatId = jdbcClient
            .sql("SELECT chat_id FROM chats WHERE id=?")
            .param(id)
            .query(Long.class)
            .single();
        Assertions.assertEquals(chatId, actualChatId);
    }
}
