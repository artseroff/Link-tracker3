package edu.java.scrapper.api.service;

import edu.java.response.LinkResponse;
import edu.java.scrapper.api.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.api.service.exception.EntityNotFoundException;
import edu.java.scrapper.api.service.exception.NotSupportedLinkException;
import java.net.URI;
import java.util.Collection;

public interface LinkService {
    LinkResponse track(long chatId, URI url)
        throws EntityAlreadyExistException, EntityNotFoundException, NotSupportedLinkException;

    LinkResponse untrack(long chatId, URI url) throws EntityNotFoundException;

    Collection<LinkResponse> listAll(long chatId) throws EntityNotFoundException;
}
