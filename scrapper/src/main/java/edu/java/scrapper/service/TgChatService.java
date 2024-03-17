package edu.java.scrapper.service;

import edu.java.scrapper.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.service.exception.EntityNotFoundException;

public interface TgChatService {
    void register(long chatId) throws EntityAlreadyExistException;

    void unregister(long chatId) throws EntityNotFoundException;
}
