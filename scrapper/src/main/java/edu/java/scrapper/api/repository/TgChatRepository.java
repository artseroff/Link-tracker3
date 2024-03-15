package edu.java.scrapper.api.repository;

import edu.java.scrapper.api.repository.dto.ChatDto;
import java.util.Collection;
import java.util.Optional;

public interface TgChatRepository {
    Optional<ChatDto> findById(long chatId);

    ChatDto add(long chatId);

    void remove(long chatId);

    Collection<ChatDto> findAll();
}
