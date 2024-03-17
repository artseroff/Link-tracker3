package edu.java.scrapper.domain.jooq.repository;

import edu.java.scrapper.domain.SubscriptionRepository;
import edu.java.scrapper.domain.dto.ChatDto;
import edu.java.scrapper.domain.dto.LinkDto;
import edu.java.scrapper.domain.dto.SubscriptionDto;
import edu.java.scrapper.domain.jooq.Tables;
import edu.java.scrapper.domain.jooq.tables.Chats;
import edu.java.scrapper.domain.jooq.tables.Subscriptions;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class JooqSubscriptionRepository implements SubscriptionRepository {

    private final DSLContext dslContext;

    public JooqSubscriptionRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }


    @Override
    public Optional<SubscriptionDto> findEntity(SubscriptionDto subscriptionDto) {
        return dslContext
            .selectFrom(Tables.SUBSCRIPTIONS)
            .where(Tables.SUBSCRIPTIONS.CHAT_ID.eq((int) subscriptionDto.chatId())
                .and(Tables.SUBSCRIPTIONS.LINK_ID.eq((int) subscriptionDto.linkId())))
            .fetchOptional()
            .map(subscriptionsRecord -> subscriptionsRecord.into(SubscriptionDto.class));
    }

    @Override
    public Collection<ChatDto> findChatsByLinkId(long linkId) {
        return dslContext
            .select(Tables.SUBSCRIPTIONS.CHAT_ID)
            .from(Tables.SUBSCRIPTIONS)
            .where(Tables.SUBSCRIPTIONS.LINK_ID.eq((int) linkId))
            .fetchInto(ChatDto.class);
    }

    @Override
    public Collection<LinkDto> findLinksByChatId(long chatId) {
        return dslContext
            .select(Tables.LINKS.fields())
            .from(Tables.SUBSCRIPTIONS)
            .join(Tables.LINKS).on(
                Tables.LINKS.ID.eq(Tables.SUBSCRIPTIONS.LINK_ID)
                    .and(Tables.SUBSCRIPTIONS.CHAT_ID.eq((int) chatId))
            )
            .orderBy(Tables.SUBSCRIPTIONS.LINK_ID)
            .fetchInto(LinkDto.class);
    }


    @Override
    public SubscriptionDto add(SubscriptionDto subscriptionDto) {
        Optional<SubscriptionDto> optionalSubscriptionDto = dslContext.insertInto(Tables.SUBSCRIPTIONS)
            .set(dslContext.newRecord(Tables.SUBSCRIPTIONS, subscriptionDto))
            .returning()
            .fetchOptional()
            .map(subscriptionsRecord -> subscriptionsRecord.into(SubscriptionDto.class));

        return optionalSubscriptionDto.get();
    }


    @Override
    public void remove(SubscriptionDto subscriptionDto) {
        dslContext
            .deleteFrom(Tables.SUBSCRIPTIONS)
            .where(Tables.SUBSCRIPTIONS.CHAT_ID.eq((int) subscriptionDto.chatId())
                .and(Tables.SUBSCRIPTIONS.LINK_ID.eq((int) subscriptionDto.linkId())))
            .execute();
    }


    @Override
    public Collection<SubscriptionDto> findAll() {
        return dslContext
            .selectFrom(Tables.SUBSCRIPTIONS)
            .fetchInto(SubscriptionDto.class);
    }
}
