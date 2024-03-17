package edu.java.scrapper.api.service.updater.github;

import edu.java.scrapper.api.service.exception.EntityNotFoundException;
import edu.java.scrapper.api.service.exception.NotSupportedLinkException;
import edu.java.scrapper.api.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.api.service.updater.LinkUpdateDescription;
import edu.java.scrapper.client.dto.github.RepositoryEventResponse;
import edu.java.scrapper.client.github.GithubClient;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GithubUpdatesFetcher extends AbstractUpdatesFetcher {
    private static final String SITE_BASE_URL = "github.com";
    private final GithubClient githubClient;

    public GithubUpdatesFetcher(
        GithubClient githubClient
    ) {
        this.githubClient = githubClient;
    }

    @Override
    public Optional<LinkUpdateDescription> fetchUpdatesFromLink(URI url, OffsetDateTime lastUpdatedAt)
        throws NotSupportedLinkException, EntityNotFoundException {

        String textPath = getProceedUrl(url, SITE_BASE_URL);

        RepoSearchRequest request = buildRepoSearchRequest(textPath);

        RepositoryEventResponse repositoryEventResponse =
            githubClient.fetchLastModified(request.owner(), request.repo());
        if (repositoryEventResponse == null) {
            throw new EntityNotFoundException("Ссылка %s указывает на несуществующий репозиторий"
                .formatted(url));
        }

        return defineShouldMakeLinkUpdate(
            textPath,
            repositoryEventResponse.createdAt(),
            lastUpdatedAt,
            "Репозиторий обновился"
        );
    }

    private RepoSearchRequest buildRepoSearchRequest(String textUrl) throws NotSupportedLinkException {
        String[] parts = textUrl.split(URL_DELIMITER);
        if (parts.length != 2) {
            throw new NotSupportedLinkException(
                "Ссылка должна указывать на репозиторий.\nПример: https://github.com/{owner}/{repo}");
        }
        return new RepoSearchRequest(parts[0], parts[1]);
    }

}
