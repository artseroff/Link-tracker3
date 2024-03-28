package edu.java.scrapper.domain;

import edu.java.scrapper.domain.dto.ChatDto;
import java.util.Collection;
import java.util.Optional;

public interface TgChatRepository {
    Optional<ChatDto> findById(long chatId);

    ChatDto add(long chatId);

    void remove(long chatId);

    Collection<ChatDto> findAll();
}
