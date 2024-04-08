package edu.java.scrapper.service;

import edu.java.scrapper.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.service.exception.EntityNotFoundException;

public interface TgChatService {
    String CHAT_NOT_FOUND = "Чат %s не найден";
    String ALREADY_REGISTERED = "Вы уже зарегистрированы";

    void register(long chatId) throws EntityAlreadyExistException;

    void unregister(long chatId) throws EntityNotFoundException;
}
