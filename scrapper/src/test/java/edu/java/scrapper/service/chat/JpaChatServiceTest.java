package edu.java.scrapper.service.chat;

import edu.java.scrapper.configuration.db.AccessType;
import edu.java.scrapper.domain.jpa.JpaTgChatRepository;
import edu.java.scrapper.domain.jpa.entity.ChatEntity;
import edu.java.scrapper.integration.IntegrationTest;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class JpaChatServiceTest extends IntegrationTest {

    private final TgChatService tgChatService;
    private final JpaTgChatRepository chatRepository;

    @Autowired
    public JpaChatServiceTest(TgChatService tgChatService, JpaTgChatRepository chatRepository) {
        this.tgChatService = tgChatService;
        this.chatRepository = chatRepository;
    }

    @DynamicPropertySource
    static void setAccessType(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> AccessType.JPA);
    }

    @Test
    public void register() throws EntityAlreadyExistException {
        // Arrange
        long expectedId = 1L;

        // Act
        tgChatService.register(expectedId);
        Optional<ChatEntity> optionalChatEntity = chatRepository.findById(expectedId);

        // Assert
        Assertions.assertTrue(optionalChatEntity.isPresent());
        Assertions.assertEquals(expectedId, optionalChatEntity.get().getId());

    }

    @Test
    public void register_AlreadyExist() {
        // Arrange
        long id = 1L;

        // Act
        try {
            tgChatService.register(id);
        } catch (EntityAlreadyExistException ignored) {
        }

        // Assert
        Assertions.assertThrows(
            EntityAlreadyExistException.class,
            () -> tgChatService.register(id)
        );
    }

    @Test
    public void unregister() throws EntityAlreadyExistException, EntityNotFoundException {
        // Arrange
        long id = 1L;

        // Act
        tgChatService.register(id);
        tgChatService.unregister(id);
        Optional<ChatEntity> optionalChatEntity = chatRepository.findById(id);

        // Assert
        Assertions.assertTrue(optionalChatEntity.isEmpty());
    }

    @Test
    public void unregister_NotFound() {
        // Arrange
        long id = 1L;

        // Act & Assert
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> tgChatService.unregister(id)
        );
    }
}
