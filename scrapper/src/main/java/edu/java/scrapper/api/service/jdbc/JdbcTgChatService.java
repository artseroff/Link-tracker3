package edu.java.scrapper.api.service.jdbc;

import edu.java.scrapper.api.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.api.service.TgChatService;
import edu.java.scrapper.api.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.api.service.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JdbcTgChatService implements TgChatService {
    private final JdbcTgChatRepository chatRepository;

    private final JdbcLinkService linkService;

    public JdbcTgChatService(JdbcTgChatRepository chatRepository, JdbcLinkService linkService) {
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
        chatRepository.remove(chatId);

        linkService.listAll(chatId)
            .forEach(linkDto -> {
                try {
                    linkService.untrack(chatId, linkDto.url());
                } catch (EntityNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
    }

}
