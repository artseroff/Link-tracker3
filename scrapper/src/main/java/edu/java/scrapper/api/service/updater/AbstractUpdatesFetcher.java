package edu.java.scrapper.api.service.updater;

import edu.java.request.LinkUpdateRequest;
import edu.java.scrapper.api.repository.LinkRepository;
import edu.java.scrapper.api.repository.SubscriptionRepository;
import edu.java.scrapper.api.repository.dto.ChatDto;
import edu.java.scrapper.api.repository.dto.LinkDto;
import edu.java.scrapper.api.service.exception.EntityNotFoundException;
import edu.java.scrapper.api.service.exception.NotSupportedLinkException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public abstract class AbstractUpdatesFetcher {
    private static final String URL_DELIMITER = "/";
    private final SubscriptionRepository subscriptionRepository;

    private final LinkRepository linkRepository;
    private AbstractUpdatesFetcher next;

    protected AbstractUpdatesFetcher(SubscriptionRepository subscriptionRepository, LinkRepository linkRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.linkRepository = linkRepository;
    }

    public void setNext(AbstractUpdatesFetcher next) {
        if (this.equals(next)) {
            throw new IllegalArgumentException("В цепочке не могут состоять одинаковые объекты");
        }
        this.next = next;
    }

    public Optional<LinkUpdateRequest> chainedUpdatesFetching(LinkDto linkDto)
        throws NotSupportedLinkException, EntityNotFoundException {
        try {
            return fetchUpdatesFromLink(linkDto);
        } catch (NotSupportedLinkException e) {
            if (next != null) {
                return next.fetchUpdatesFromLink(linkDto);
            } else {
                throw e;
            }
        }
    }

    public abstract Optional<LinkUpdateRequest> fetchUpdatesFromLink(LinkDto linkDto)
        throws NotSupportedLinkException, EntityNotFoundException;

    protected String[] getUrlPathParts(URI url, String siteBaseUrl) throws NotSupportedLinkException {
        FetchersChainUtils.throwIfProtocolAbsent(url.toString());
        if (!siteBaseUrl.equals(url.getHost())) {
            throw new NotSupportedLinkException("Сервис %s не поддерживается".formatted(url.getHost()));
        }
        String path = url.getPath();
        int startIndex = 0;
        if (path.startsWith(URL_DELIMITER)) {
            startIndex = 1;
        }
        int endIndex = path.length();
        if (path.endsWith(URL_DELIMITER)) {
            endIndex--;
        }
        String substring = path.substring(startIndex, endIndex);

        return substring.split(URL_DELIMITER);
    }

    protected Optional<LinkUpdateRequest> makeLinkUpdate(
        LinkDto linkDto,
        OffsetDateTime fetchedUpdateDate,
        String description
    ) {
        OffsetDateTime checkedTime = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime lastUpdatedAt = linkDto.lastUpdatedAt();

        boolean dontNeedUpdate = lastUpdatedAt != null
            && (fetchedUpdateDate.isEqual(lastUpdatedAt) || fetchedUpdateDate.isBefore(lastUpdatedAt));

        if (dontNeedUpdate) {
            return Optional.empty();
        }

        linkRepository.updateModifiedAndSchedulerCheckDates(linkDto.id(), fetchedUpdateDate, checkedTime);

        List<Long> chatIds = subscriptionRepository.findChatsByLinkId(linkDto.id()).stream()
            .map(ChatDto::id)
            .toList();

        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(linkDto.id(), linkDto.url(), description, chatIds);
        return Optional.of(linkUpdateRequest);
    }
}
