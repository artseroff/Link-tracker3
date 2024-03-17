package edu.java.scrapper.domain;

import edu.java.scrapper.domain.dto.ChatDto;
import edu.java.scrapper.integration.IntegrationTest;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class AbstractTgChatRepositoryTest extends IntegrationTest {
    private final TgChatRepository chatRepository;

    protected AbstractTgChatRepositoryTest(TgChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @BeforeEach
    public abstract void truncateTableChats();

    @Test
    void findById_NotExistedTest() {
        // Arrange
        long chatId = 1L;

        // Act
        Optional<ChatDto> foundChat = chatRepository.findById(chatId);

        // Assert
        Assertions.assertFalse(foundChat.isPresent());
    }

    @Test
    void addThenFindTest() {
        // Arrange
        long expectedChatId = 1L;

        // Act
        chatRepository.add(expectedChatId);
        Optional<ChatDto> actualChat = chatRepository.findById(expectedChatId);

        // Assert
        Assertions.assertTrue(actualChat.isPresent());
        Assertions.assertEquals(actualChat.get().id(), expectedChatId);
    }

    @Test
    void addThenRemoveTest() {
        // Arrange
        long chatId = 1L;

        // Act
        chatRepository.add(chatId);
        chatRepository.remove(chatId);
        Optional<ChatDto> chat = chatRepository.findById(chatId);

        // Assert
        Assertions.assertFalse(chat.isPresent());
    }

    @Test
    void remove_NotExistedTest() {
        // Arrange
        long chatId = 1L;

        // Act & Assert
        Assertions.assertDoesNotThrow(() -> chatRepository.remove(chatId));
    }

    @Test
    void findAllTest() {
        // Arrange
        ChatDto chat1 = new ChatDto(1L);
        ChatDto chat2 = new ChatDto(2L);
        List<ChatDto> expectedChats = List.of(chat1, chat2);

        // Act
        chatRepository.add(chat1.id());
        chatRepository.add(chat2.id());
        Collection<ChatDto> actualChats = chatRepository.findAll();

        // Assert
        Assertions.assertEquals(expectedChats, actualChats);
    }
}
