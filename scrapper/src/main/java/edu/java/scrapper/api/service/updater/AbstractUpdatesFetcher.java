package edu.java.scrapper.api.service.updater;

import edu.java.scrapper.api.service.exception.EntityNotFoundException;
import edu.java.scrapper.api.service.exception.NotSupportedLinkException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

public abstract class AbstractUpdatesFetcher {
    protected static final String URL_DELIMITER = "/";
    private AbstractUpdatesFetcher next;

    public void setNext(AbstractUpdatesFetcher next) {
        if (this.equals(next)) {
            throw new IllegalArgumentException("В цепочке не могут состоять одинаковые объекты");
        }
        this.next = next;
    }

    public Optional<LinkUpdateDescription> chainedUpdatesFetching(URI url, OffsetDateTime lastUpdatedAt)
        throws NotSupportedLinkException, EntityNotFoundException {
        try {
            return fetchUpdatesFromLink(url, lastUpdatedAt);
        } catch (NotSupportedLinkException e) {
            if (next != null) {
                return next.fetchUpdatesFromLink(url, lastUpdatedAt);
            } else {
                throw e;
            }
        }
    }

    /**
     * Извлекает обновления по ссылке
     *
     * @param url           адрес ресурса
     * @param lastUpdatedAt дата последнего обновления
     * @return Optional.empty(), в случае если не появились обновления.
     *     В ином случае:
     *     Optional обертку над LinkUpdateDescription с информацией об провалидированном url,
     *     новой датой последнего обновления, временем проверки изменений и описанием изменений
     * @throws NotSupportedLinkException Если ссылка не поддерживается объектом этого класса
     * @throws EntityNotFoundException   Если ресурс по ссылке не найден
     */
    public abstract Optional<LinkUpdateDescription> fetchUpdatesFromLink(URI url, OffsetDateTime lastUpdatedAt)
        throws NotSupportedLinkException, EntityNotFoundException;

    protected String getProceedUrl(URI url, String siteBaseUrl) throws NotSupportedLinkException {
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

        return path.substring(startIndex, endIndex);
    }

    protected Optional<LinkUpdateDescription> defineShouldMakeLinkUpdate(
        String urlPath,
        OffsetDateTime fetchedUpdateDate,
        OffsetDateTime lastUpdatedAt,
        String description
    ) {
        OffsetDateTime checkedTime = OffsetDateTime.now(ZoneOffset.UTC);

        boolean dontNeedUpdate = lastUpdatedAt != null
            && (fetchedUpdateDate.isEqual(lastUpdatedAt) || fetchedUpdateDate.isBefore(lastUpdatedAt));

        if (dontNeedUpdate) {
            return Optional.empty();
        }

        URI fullUrl = URI.create(FetchersChainUtils.SECURE_HYPER_TEXT_PROTOCOL + urlPath);
        LinkUpdateDescription linkUpdateDescription =
            new LinkUpdateDescription(fullUrl, fetchedUpdateDate, checkedTime, description);
        return Optional.of(linkUpdateDescription);
    }
}
