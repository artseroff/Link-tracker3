package edu.java.bot.service;

import edu.java.bot.exception.AlreadyTrackedLinkException;
import edu.java.bot.exception.NotTrackedLinkException;
import edu.java.bot.exception.UserNotFoundException;
import edu.java.bot.service.link.LinkUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private final Map<Long, List<String>> map = new HashMap<>();

    public boolean isUserRegistered(long id) {
        return map.containsKey(id);
    }

    public List<String> listTrackedLinkByUserId(long userId) throws UserNotFoundException {
        if (!isUserRegistered(userId)) {
            throw new UserNotFoundException("Пользователь не зарегистрирован");
        }
        return map.get(userId);
    }

    public boolean addUser(long id) {
        if (isUserRegistered(id)) {
            return false;
        }
        map.put(id, new ArrayList<>());
        return true;
    }

    public void addLink(long userId, String link) throws UserNotFoundException, AlreadyTrackedLinkException {
        LinkUtils.checkLinkCorrectnessOrThrow(link);
        List<String> links = listTrackedLinkByUserId(userId);
        if (links.contains(link)) {
            throw new AlreadyTrackedLinkException("Указанная ссылка уже отслеживается");
        }
        links.add(link);
    }

    public void deleteLink(long userId, String link)
        throws UserNotFoundException, NotTrackedLinkException {
        LinkUtils.checkLinkCorrectnessOrThrow(link);
        List<String> links = listTrackedLinkByUserId(userId);
        if (!links.contains(link)) {
            throw new NotTrackedLinkException("Указанной ссылки нет в списке отслеживаемых");
        }
        links.remove(link);
    }

    public static UserService getInstance() {
        return UserServiceHolder.INSTANCE;
    }

    private static class UserServiceHolder {
        private static final UserService INSTANCE = new UserService();
    }

}

