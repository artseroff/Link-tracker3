package edu.java.scrapper.service.impl.jpa;

import edu.java.response.LinkResponse;
import edu.java.scrapper.domain.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.jpa.JpaTgChatRepository;
import edu.java.scrapper.domain.jpa.entity.ChatEntity;
import edu.java.scrapper.domain.jpa.entity.LinkEntity;
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
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaLinkService implements LinkService {
    private final JpaLinkRepository linkRepository;
    private final AbstractUpdatesFetcher headUpdatesFetcher;
    private final JpaTgChatRepository jpaTgChatRepository;

    public JpaLinkService(
        JpaLinkRepository linkRepository,
        AbstractUpdatesFetcher headUpdatesFetcher,
        JpaTgChatRepository jpaTgChatRepository
    ) {
        this.linkRepository = linkRepository;
        this.headUpdatesFetcher = headUpdatesFetcher;
        this.jpaTgChatRepository = jpaTgChatRepository;
    }

    @Override
    public LinkResponse track(long chatId, URI url)
        throws EntityAlreadyExistException, EntityNotFoundException, NotSupportedLinkException, CorruptedLinkException {

        ChatEntity chatEntity = checkIsChatRegisteredOrThrow(chatId);

        LinkEntity linkEntity = addLinkIfNotExist(url);

        boolean subscriptionExists = linkEntity.getChats()
            .contains(chatEntity);

        if (subscriptionExists) {
            throw new EntityAlreadyExistException(ALREADY_TRACKED_LINK.formatted(linkEntity.getUrl()));
        }

        linkEntity.getChats().add(chatEntity);
        linkRepository.save(linkEntity);

        return new LinkResponse(linkEntity.getId(), linkEntity.getUrl());
    }

    private ChatEntity checkIsChatRegisteredOrThrow(long chatId) throws EntityNotFoundException {
        return jpaTgChatRepository.findById(chatId)
            .orElseThrow(() -> new EntityNotFoundException(NEED_REGISTRATION));
    }

    private LinkEntity addLinkIfNotExist(URI url)
        throws NotSupportedLinkException, EntityNotFoundException, CorruptedLinkException {
        URI proceedUrl = LinkService.deleteTrailingSlash(url);
        Optional<LinkEntity> linkEntity = linkRepository.findByUrl(proceedUrl);
        if (linkEntity.isPresent()) {
            return linkEntity.get();
        }

        Optional<LinkUpdateDescription> updateDescriptionOptional =
            headUpdatesFetcher.chainedUpdatesFetching(proceedUrl, null);

        if (updateDescriptionOptional.isEmpty()) {
            throw new RuntimeException("Не удалось выполнить запрос по ссылке %s".formatted(url));
        }

        LinkUpdateDescription updateDescription = updateDescriptionOptional.get();

        // Повторная проверка по обработанному url
        linkEntity = linkRepository.findByUrl(updateDescription.url());
        if (linkEntity.isPresent()) {
            return linkEntity.get();
        }

        LinkEntity linkForSave = new LinkEntity();
        linkForSave.setUrl(updateDescription.url());
        linkForSave.setLastUpdatedAt(updateDescription.lastUpdatedAt());
        linkForSave.setLastSchedulerCheck(updateDescription.lastSchedulerCheck());
        return linkRepository.save(linkForSave);
    }

    @Override
    public LinkResponse untrack(long chatId, URI url) throws EntityNotFoundException {
        ChatEntity chatEntity = checkIsChatRegisteredOrThrow(chatId);

        URI proceedUrl = LinkService.deleteTrailingSlash(url);
        LinkEntity linkEntity = linkRepository.findByUrl(proceedUrl)
            .orElseThrow(() -> new EntityNotFoundException(NOT_TRACKED_LINK.formatted(url)));

        Set<ChatEntity> chats = linkEntity.getChats();
        boolean subscriptionExists = chats
            .contains(chatEntity);

        if (!subscriptionExists) {
            throw new EntityNotFoundException(NOT_TRACKED_LINK.formatted(url));
        }

        chats.remove(chatEntity);

        if (chats.isEmpty()) {
            linkRepository.delete(linkEntity);
        } else {
            linkEntity.getChats().remove(chatEntity);
            linkRepository.save(linkEntity);
        }

        return new LinkResponse(linkEntity.getId(), linkEntity.getUrl());
    }

    @Override
    public Collection<LinkResponse> listAll(long chatId) throws EntityNotFoundException {
        checkIsChatRegisteredOrThrow(chatId);
        return linkRepository.findAllByChat(chatId).stream()
            .map(linkEntity -> new LinkResponse(linkEntity.getId(), linkEntity.getUrl()))
            .toList();
    }
}
