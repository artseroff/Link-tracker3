package edu.java.scrapper.service.impl.jpa;

import edu.java.scrapper.domain.jpa.JpaTgChatRepository;
import edu.java.scrapper.domain.jpa.entity.ChatEntity;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import java.net.URI;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaTgChatService implements TgChatService {
    private final JpaTgChatRepository chatRepository;

    private final LinkService linkService;

    public JpaTgChatService(
        JpaTgChatRepository chatRepository,
        LinkService linkService
    ) {
        this.chatRepository = chatRepository;
        this.linkService = linkService;
    }

    @Override
    public void register(long chatId) throws EntityAlreadyExistException {
        if (chatRepository.findById(chatId).isPresent()) {
            throw new EntityAlreadyExistException("Чат уже зарегистрирован");
        }
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setId(chatId);
        chatRepository.save(chatEntity);
    }

    @Override
    public void unregister(long chatId) throws EntityNotFoundException {
        Optional<ChatEntity> optionalChat = chatRepository.findById(chatId);

        optionalChat.orElseThrow(() -> new EntityNotFoundException("Чат не найден"));

        optionalChat.get().getLinks()
            .forEach(linkEntity -> {
                try {
                    linkService.untrack(chatId, URI.create(linkEntity.getUrl()));
                } catch (EntityNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });

        chatRepository.deleteById(chatId);
    }

}
