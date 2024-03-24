package edu.java.scrapper.service.chat;

import edu.java.scrapper.domain.TgChatRepository;
import edu.java.scrapper.domain.dto.ChatDto;
import edu.java.scrapper.integration.IntegrationTest;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public abstract class AbstractSimpleChatServiceTest extends IntegrationTest {

    private final TgChatService tgChatService;
    private final TgChatRepository chatRepository;

    public AbstractSimpleChatServiceTest(TgChatService tgChatService, TgChatRepository chatRepository) {
        this.tgChatService = tgChatService;
        this.chatRepository = chatRepository;
    }

    @Test
    public void register() throws EntityAlreadyExistException {
        // Arrange
        long expectedId = 1L;

        // Act
        tgChatService.register(expectedId);
        Optional<ChatDto> optionalChatDto = chatRepository.findById(expectedId);

        // Assert
        Assertions.assertTrue(optionalChatDto.isPresent());
        Assertions.assertEquals(expectedId, optionalChatDto.get().id());

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
        Optional<ChatDto> optionalChatDto = chatRepository.findById(id);

        // Assert
        Assertions.assertTrue(optionalChatDto.isEmpty());
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
