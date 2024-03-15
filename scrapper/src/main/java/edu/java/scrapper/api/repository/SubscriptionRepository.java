package edu.java.scrapper.api.repository;

import edu.java.scrapper.api.repository.dto.ChatDto;
import edu.java.scrapper.api.repository.dto.LinkDto;
import edu.java.scrapper.api.repository.dto.SubscriptionDto;
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
