package edu.java.scrapper.domain;

import edu.java.scrapper.domain.dto.ChatDto;
import edu.java.scrapper.domain.dto.LinkDto;
import edu.java.scrapper.domain.dto.SubscriptionDto;
import java.util.Collection;
import java.util.Optional;

public interface SubscriptionRepository {
    SubscriptionDto add(SubscriptionDto subscriptionDto);

    void remove(SubscriptionDto subscriptionDto);

    Collection<SubscriptionDto> findAll();

    Optional<SubscriptionDto> findEntity(SubscriptionDto subscriptionDto);

    Collection<ChatDto> findChatsByLinkId(long linkId);

    Collection<LinkDto> findLinksByChatId(long chatId);
}
