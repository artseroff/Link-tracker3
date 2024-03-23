package edu.java.scrapper.domain.jdbc;

import edu.java.scrapper.domain.SubscriptionRepository;
import edu.java.scrapper.domain.dto.ChatDto;
import edu.java.scrapper.domain.dto.LinkDto;
import edu.java.scrapper.domain.dto.SubscriptionDto;
import java.util.Collection;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JdbcSubscriptionRepository implements SubscriptionRepository {

    private final JdbcClient jdbcClient;

    public JdbcSubscriptionRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<SubscriptionDto> findEntity(SubscriptionDto subscriptionDto) {
        return jdbcClient.sql("SELECT * FROM subscriptions WHERE chat_id = ? AND link_id = ?")
            .param(subscriptionDto.chatId())
            .param(subscriptionDto.linkId())
            .query(SubscriptionDto.class)
            .optional();
    }

    @Override
    public Collection<ChatDto> findChatsByLinkId(long linkId) {
        return jdbcClient.sql("SELECT chat_id AS id FROM subscriptions WHERE link_id = ?")
            .param(linkId)
            .query(ChatDto.class)
            .list();
    }

    @Override
    public Collection<LinkDto> findLinksByChatId(long chatId) {
        String sqlText = """
            SELECT links.*
                FROM subscriptions Inner Join links
                    on links.id = subscriptions.link_id
                           AND subscriptions.chat_id = ?
                ORDER BY link_id
            """;
        return jdbcClient.sql(sqlText)
            .param(chatId)
            .query(LinkDto.class)
            .list();
    }

    @Override
    public SubscriptionDto add(SubscriptionDto subscriptionDto) {
        jdbcClient.sql("INSERT INTO subscriptions (chat_id,link_id) VALUES (?,?)")
            .param(subscriptionDto.chatId())
            .param(subscriptionDto.linkId())
            .update();
        return subscriptionDto;
    }

    @Override
    public void remove(SubscriptionDto subscriptionDto) {
        jdbcClient.sql("DELETE FROM subscriptions WHERE chat_id = ? AND link_id = ?")
            .param(subscriptionDto.chatId())
            .param(subscriptionDto.linkId())
            .update();
    }

    @Override
    public Collection<SubscriptionDto> findAll() {
        return jdbcClient.sql("SELECT * FROM subscriptions")
            .query(SubscriptionDto.class)
            .list();
    }
}
