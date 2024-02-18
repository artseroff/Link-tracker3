package edu.java.bot.service;

import edu.java.bot.exception.AlreadyTrackedLinkException;
import edu.java.bot.exception.NotTrackedLinkException;
import edu.java.bot.exception.UserNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final Map<Long, List<String>> map = new HashMap<>();

    public boolean isUserRegistered(long id) {
        return map.containsKey(id);
    }

    public List<String> listTrackedLinkByUserId(long userId) throws UserNotFoundException {
        if (!isUserRegistered(userId)) {
            throw new UserNotFoundException("Вы не зарегистрированы");
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
        List<String> links = listTrackedLinkByUserId(userId);
        if (links.contains(link)) {
            throw new AlreadyTrackedLinkException("Указанная ссылка уже отслеживается");
        }
        links.add(link);
    }

    public void deleteLink(long userId, String link)
        throws UserNotFoundException, NotTrackedLinkException {
        List<String> links = listTrackedLinkByUserId(userId);
        if (!links.contains(link)) {
            throw new NotTrackedLinkException("Указанной ссылки нет в списке отслеживаемых");
        }
        links.remove(link);
    }

    public void clear() {
        map.clear();
    }

}

