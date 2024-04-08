package edu.java.scrapper.service.chat;

import edu.java.scrapper.configuration.db.AccessType;
import edu.java.scrapper.domain.TgChatRepository;
import edu.java.scrapper.service.TgChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
public class JooqSimpleChatServiceTest extends AbstractSimpleChatServiceTest {

    @Autowired
    public JooqSimpleChatServiceTest(TgChatService tgChatService, TgChatRepository chatRepository) {
        super(tgChatService, chatRepository);
    }

    @DynamicPropertySource
    static void setAccessType(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> AccessType.JOOQ);
    }
}
