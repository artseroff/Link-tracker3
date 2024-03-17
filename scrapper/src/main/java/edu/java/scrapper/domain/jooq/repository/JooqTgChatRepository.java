package edu.java.scrapper.domain.jooq.repository;

import edu.java.scrapper.domain.TgChatRepository;
import edu.java.scrapper.domain.dto.ChatDto;
import edu.java.scrapper.domain.jooq.Tables;
import edu.java.scrapper.domain.jooq.tables.Chats;
import edu.java.scrapper.domain.jooq.tables.records.ChatsRecord;
import org.jooq.DSLContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.Optional;

@Repository
public class JooqTgChatRepository implements TgChatRepository {
    private final DSLContext dslContext;
    private final JdbcClient jdbcClient;

    public JooqTgChatRepository(DSLContext dslContext, JdbcClient jdbcClient) {
        this.dslContext = dslContext;
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<ChatDto> findById(long chatId) {
        return dslContext
            .selectFrom(Tables.CHATS)
            .where(Chats.CHATS.ID.eq(chatId))
            .fetchOptional()
            .map(chatsRecord -> chatsRecord.into(ChatDto.class));
    }

    @Override
    public ChatDto add(long chatId) {
        Optional<ChatDto> chatDto = dslContext.insertInto(Tables.CHATS)
            .values(chatId)
            .returning()
            .fetchOptional()
            .map(chatsRecord -> chatsRecord.into(ChatDto.class));

        return chatDto.get();
    }

    @Override
    public void remove(long chatId) {
        dslContext
            .deleteFrom(Tables.CHATS)
            .where(Tables.CHATS.ID.eq(chatId))
            .execute();
    }

    @Override
    public Collection<ChatDto> findAll() {
        return dslContext
            .selectFrom(Tables.CHATS)
            .fetchInto(ChatDto.class);
    }
}
