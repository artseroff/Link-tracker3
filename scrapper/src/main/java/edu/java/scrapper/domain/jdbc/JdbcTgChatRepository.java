package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.domain.TgChatRepository;
import edu.java.scrapper.domain.dto.ChatDto;
import java.util.Collection;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTgChatRepository implements TgChatRepository {

    private final JdbcClient jdbcClient;

    public JdbcTgChatRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<ChatDto> findById(long chatId) {
        return jdbcClient.sql("SELECT * FROM chats WHERE id = ?")
            .param(chatId)
            .query(ChatDto.class)
            .optional();
    }

    @Override
    public ChatDto add(long chatId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("INSERT INTO chats (id) VALUES (?)")
            .param(chatId)
            .update(keyHolder);
        long id = keyHolder.getKey().longValue();
        return new ChatDto(id);
    }

    @Override
    public void remove(long chatId) {
        jdbcClient.sql("DELETE FROM chats WHERE id = ?")
            .param(chatId)
            .update();
    }

    @Override
    public Collection<ChatDto> findAll() {
        return jdbcClient.sql("SELECT * FROM chats")
            .query(ChatDto.class)
            .list();
    }
}
