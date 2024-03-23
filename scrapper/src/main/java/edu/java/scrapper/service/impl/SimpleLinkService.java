package edu.java.scrapper.service.impl;

import edu.java.response.LinkResponse;
import edu.java.scrapper.domain.LinkRepository;
import edu.java.scrapper.domain.SubscriptionRepository;
import edu.java.scrapper.domain.TgChatRepository;
import edu.java.scrapper.domain.dto.ChatDto;
import edu.java.scrapper.domain.dto.LinkDto;
import edu.java.scrapper.domain.dto.SubscriptionDto;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.exception.CorruptedLinkException;
import edu.java.scrapper.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import edu.java.scrapper.service.exception.NotSupportedLinkException;
import edu.java.scrapper.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.service.updater.LinkUpdateDescription;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SimpleLinkService implements LinkService {
    private final TgChatRepository chatRepository;
    private final LinkRepository linkRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AbstractUpdatesFetcher headUpdatesFetcher;

    public SimpleLinkService(
        TgChatRepository chatRepository,
        LinkRepository linkRepository,
        SubscriptionRepository subscriptionRepository,
        AbstractUpdatesFetcher headUpdatesFetcher
    ) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.headUpdatesFetcher = headUpdatesFetcher;
    }

    @Override
    public LinkResponse track(long chatId, URI url)
        throws EntityAlreadyExistException, EntityNotFoundException, NotSupportedLinkException, CorruptedLinkException {
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

    private LinkDto addLinkIfNotExist(URI url)
        throws NotSupportedLinkException, EntityNotFoundException, CorruptedLinkException {
        URI proceedUrl = LinkService.deleteTrailingSlash(url);
        Optional<LinkDto> linkDto = linkRepository.findByUrl(proceedUrl);
        if (linkDto.isPresent()) {
            return linkDto.get();
        }

        Optional<LinkUpdateDescription> updateDescriptionOptional =
            headUpdatesFetcher.chainedUpdatesFetching(proceedUrl, null);

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

        URI proceedUrl = LinkService.deleteTrailingSlash(url);
        LinkDto linkDto = linkRepository.findByUrl(proceedUrl)
            .orElseThrow(() -> new EntityNotFoundException(NOT_TRACKED_LINK.formatted(url)));
        SubscriptionDto subscription =
            subscriptionRepository.findEntity(new SubscriptionDto(chatId, linkDto.id()))
                .orElseThrow(() -> new EntityNotFoundException(NOT_TRACKED_LINK.formatted(url)));
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
