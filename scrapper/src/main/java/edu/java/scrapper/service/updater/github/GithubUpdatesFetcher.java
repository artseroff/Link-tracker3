package edu.java.scrapper.service.updater.github;

import edu.java.scrapper.client.dto.github.CommitInfoResponse;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.service.exception.CorruptedLinkException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import edu.java.scrapper.service.exception.NotSupportedLinkException;
import edu.java.scrapper.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.service.updater.FetchersChainUtils;
import edu.java.scrapper.service.updater.LinkUpdateDescription;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.stereotype.Component;
import static edu.java.scrapper.service.updater.FetchersChainUtils.URL_DELIMITER;

@Component
public class GithubUpdatesFetcher extends AbstractUpdatesFetcher {
    private static final String SITE_BASE_URL = "github.com";
    private static final int MESSAGE_BOUND = 60;
    private final GithubClient githubClient;

    public GithubUpdatesFetcher(
        GithubClient githubClient
    ) {
        this.githubClient = githubClient;
    }

    @Override
    public Optional<LinkUpdateDescription> fetchUpdatesFromLink(URI url, OffsetDateTime lastUpdatedAt)
        throws NotSupportedLinkException, EntityNotFoundException, CorruptedLinkException {

        String textPath = getProceedUrlPath(url, SITE_BASE_URL);

        RepoSearchRequest request = buildRepoSearchRequest(textPath);

        CommitInfoResponse commitInfoResponse = githubClient.fetchLastModifiedCommit(request.owner(), request.repo());
        if (commitInfoResponse == null) {
            throw new EntityNotFoundException("Ссылка %s указывает на несуществующий репозиторий"
                .formatted(url));
        }

        URI proceedUrl = url;
        if (!textPath.equals(url.getPath())) {
            proceedUrl =
                FetchersChainUtils.createUrl(FetchersChainUtils.SECURE_HYPER_TEXT_PROTOCOL, SITE_BASE_URL, textPath);
        }

        String description =
            FetchersChainUtils.makeStringLessThanBound(commitInfoResponse.getDescription(), MESSAGE_BOUND);
        return defineShouldMakeLinkUpdate(
            proceedUrl,
            commitInfoResponse.getLastModifiedDate(),
            lastUpdatedAt,
            description
        );
    }

    private RepoSearchRequest buildRepoSearchRequest(String textUrl)
        throws CorruptedLinkException {
        String[] parts = textUrl.split(URL_DELIMITER);
        if (parts.length != 2) {
            throw new CorruptedLinkException(
                "Ссылка должна указывать на репозиторий.\nПример: https://github.com/{owner}/{repo}");
        }
        return new RepoSearchRequest(parts[0], parts[1]);
    }

}
