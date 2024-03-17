package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.domain.TgChatRepository;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SimpleTgChatService implements TgChatService {
    private final TgChatRepository chatRepository;

    private final SimpleLinkService linkService;

    public SimpleTgChatService(
        @Qualifier("jooqTgChatRepository") TgChatRepository chatRepository,
        SimpleLinkService linkService
    ) {
        this.chatRepository = chatRepository;
        this.linkService = linkService;
    }

    @Override
    public void register(long chatId) throws EntityAlreadyExistException {
        if (chatRepository.findById(chatId).isPresent()) {
            throw new EntityAlreadyExistException("Чат уже зарегистрирован");
        }
        chatRepository.add(chatId);
    }

    @Override
    public void unregister(long chatId) throws EntityNotFoundException {
        chatRepository.findById(chatId)
            .orElseThrow(() -> new EntityNotFoundException("Чат не найден"));

        linkService.listAll(chatId)
            .forEach(linkDto -> {
                try {
                    linkService.untrack(chatId, linkDto.url());
                } catch (EntityNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });

        chatRepository.remove(chatId);
    }

}
