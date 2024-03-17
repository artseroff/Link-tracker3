package edu.java.scrapper.api.service.jdbc;

import edu.java.response.LinkResponse;
import edu.java.scrapper.api.repository.dto.ChatDto;
import edu.java.scrapper.api.repository.dto.LinkDto;
import edu.java.scrapper.api.repository.dto.SubscriptionDto;
import edu.java.scrapper.api.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.api.repository.jdbc.JdbcSubscriptionRepository;
import edu.java.scrapper.api.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.api.service.LinkService;
import edu.java.scrapper.api.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.api.service.exception.EntityNotFoundException;
import edu.java.scrapper.api.service.exception.NotSupportedLinkException;
import edu.java.scrapper.api.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.api.service.updater.LinkUpdateDescription;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JdbcLinkService implements LinkService {
    private static final String CHAT_NOT_FOUND = "Чат %d не найден";
    private static final String DONT_TRACK_LINK = "Ссылка %s вами не отслеживается";
    private static final String ALREADY_TRACKED_LINK = "Вы уже отслеживаете ссылку %s";

    private final JdbcTgChatRepository chatRepository;
    private final JdbcLinkRepository linkRepository;
    private final JdbcSubscriptionRepository subscriptionRepository;
    private final AbstractUpdatesFetcher headUpdatesFetcher;

    public JdbcLinkService(
        JdbcTgChatRepository chatRepository,
        JdbcLinkRepository linkRepository,
        JdbcSubscriptionRepository subscriptionRepository,
        @Qualifier("headUpdatesFetcher") AbstractUpdatesFetcher headUpdatesFetcher
    ) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.headUpdatesFetcher = headUpdatesFetcher;
    }

    @Override
    public LinkResponse track(long chatId, URI url)
        throws EntityAlreadyExistException, EntityNotFoundException, NotSupportedLinkException {
        checkIsChatRegisteredOrThrow(chatId);

        LinkDto linkDto = addLinkIfNotExist(url);
        SubscriptionDto subscriptionDto = new SubscriptionDto(chatId, linkDto.id());
        Optional<SubscriptionDto> foundSub = subscriptionRepository.findEntity(subscriptionDto);
        if (foundSub.isPresent()) {
            throw new EntityAlreadyExistException(ALREADY_TRACKED_LINK.formatted(linkDto.url()));
        }

        subscriptionRepository.add(subscriptionDto);
        return new LinkResponse(linkDto.id(), linkDto.url());
    }

    private void checkIsChatRegisteredOrThrow(long chatId) throws EntityNotFoundException {
        chatRepository.findById(chatId)
            .orElseThrow(() -> new EntityNotFoundException(CHAT_NOT_FOUND.formatted(chatId)));
    }

    private LinkDto addLinkIfNotExist(URI url) throws NotSupportedLinkException, EntityNotFoundException {
        Optional<LinkDto> linkDto = linkRepository.findByUrl(url);
        if (linkDto.isPresent()) {
            return linkDto.get();
        }

        Optional<LinkUpdateDescription> updateDescriptionOptional =
            headUpdatesFetcher.chainedUpdatesFetching(url, null);

        if (updateDescriptionOptional.isEmpty()) {
            throw new RuntimeException("Не удалось выполнить запрос по ссылке %s".formatted(url));
        }

        LinkUpdateDescription updateDescription = updateDescriptionOptional.get();

        // Повторная проверка по обработанному url
        linkDto = linkRepository.findByUrl(updateDescription.url());
        if (linkDto.isPresent()) {
            return linkDto.get();
        }

        return linkRepository.add(
            updateDescription.url(),
            updateDescription.lastUpdatedAt(),
            updateDescription.lastSchedulerCheck()
        );
    }

    @Override
    public LinkResponse untrack(long chatId, URI url) throws EntityNotFoundException {
        checkIsChatRegisteredOrThrow(chatId);

        LinkDto linkDto = linkRepository.findByUrl(url)
            .orElseThrow(() -> new EntityNotFoundException(DONT_TRACK_LINK.formatted(url)));
        SubscriptionDto subscription =
            subscriptionRepository.findEntity(new SubscriptionDto(chatId, linkDto.id()))
                .orElseThrow(() -> new EntityNotFoundException(DONT_TRACK_LINK.formatted(url)));
        subscriptionRepository.remove(subscription);

        deleteLinkIfNoOtherChatSubscribed(linkDto.id());
        return new LinkResponse(linkDto.id(), linkDto.url());
    }

    private void deleteLinkIfNoOtherChatSubscribed(long linkId) {
        Collection<ChatDto> chatsByLinkId = subscriptionRepository.findChatsByLinkId(linkId);
        if (chatsByLinkId.isEmpty()) {
            linkRepository.remove(linkId);
        }
    }

    @Override
    public Collection<LinkResponse> listAll(long chatId) throws EntityNotFoundException {
        checkIsChatRegisteredOrThrow(chatId);
        return subscriptionRepository.findLinksByChatId(chatId).stream()
            .map(linkDto -> new LinkResponse(linkDto.id(), linkDto.url()))
            .toList();
    }

}
