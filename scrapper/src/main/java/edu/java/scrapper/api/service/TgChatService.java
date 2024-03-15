package edu.java.scrapper.api.service;

import edu.java.scrapper.api.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.api.service.exception.EntityNotFoundException;

public interface TgChatService {
    void register(long chatId) throws EntityAlreadyExistException;

    void unregister(long chatId) throws EntityNotFoundException;
}
