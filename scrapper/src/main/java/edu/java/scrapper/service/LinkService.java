package edu.java.scrapper.service;

import edu.java.response.LinkResponse;
import edu.java.scrapper.service.exception.CorruptedLinkException;
import edu.java.scrapper.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import edu.java.scrapper.service.exception.NotSupportedLinkException;
import java.net.URI;
import java.util.Collection;

public interface LinkService {
    LinkResponse track(long chatId, URI url)
        throws EntityAlreadyExistException, EntityNotFoundException, NotSupportedLinkException, CorruptedLinkException;

    LinkResponse untrack(long chatId, URI url) throws EntityNotFoundException;

    Collection<LinkResponse> listAll(long chatId) throws EntityNotFoundException;
}
