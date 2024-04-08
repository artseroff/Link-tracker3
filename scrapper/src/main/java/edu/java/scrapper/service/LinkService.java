package edu.java.scrapper.service;

import edu.java.response.LinkResponse;
import edu.java.scrapper.service.exception.CorruptedLinkException;
import edu.java.scrapper.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import edu.java.scrapper.service.exception.NotSupportedLinkException;
import edu.java.scrapper.service.updater.FetchersChainUtils;
import java.net.URI;
import java.util.Collection;

public interface LinkService {
    String NEED_REGISTRATION = "Вы не зарегистрированы";
    String NOT_TRACKED_LINK = "Ссылка %s вами не отслеживается";
    String ALREADY_TRACKED_LINK = "Вы уже отслеживаете ссылку %s";

    LinkResponse track(long chatId, URI url)
        throws EntityAlreadyExistException, EntityNotFoundException, NotSupportedLinkException, CorruptedLinkException;

    LinkResponse untrack(long chatId, URI url) throws EntityNotFoundException;

    Collection<LinkResponse> listAll(long chatId) throws EntityNotFoundException;

    static URI deleteTrailingSlash(URI url) {
        String fullPath = url.toString().trim();
        if (!fullPath.endsWith(FetchersChainUtils.URL_DELIMITER)) {
            return URI.create(fullPath);
        }
        fullPath = fullPath.substring(0, fullPath.length() - 1);
        return URI.create(fullPath);

    }
}
