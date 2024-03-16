package edu.java.scrapper.api.service.updater.github;

import edu.java.request.LinkUpdateRequest;
import edu.java.scrapper.api.repository.LinkRepository;
import edu.java.scrapper.api.repository.SubscriptionRepository;
import edu.java.scrapper.api.repository.dto.LinkDto;
import edu.java.scrapper.api.service.exception.EntityNotFoundException;
import edu.java.scrapper.api.service.exception.NotSupportedLinkException;
import edu.java.scrapper.api.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.client.dto.github.RepositoryEventResponse;
import edu.java.scrapper.client.github.GithubClient;
import java.net.URI;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GithubUpdatesFetcher extends AbstractUpdatesFetcher {
    private static final String SITE_BASE_URL = "github.com";
    private final GithubClient githubClient;

    public GithubUpdatesFetcher(
        SubscriptionRepository subscriptionRepository,
        LinkRepository linkRepository,
        GithubClient githubClient
    ) {
        super(subscriptionRepository, linkRepository);
        this.githubClient = githubClient;
    }

    @Override
    public Optional<LinkUpdateRequest> fetchUpdatesFromLink(LinkDto linkDto)
        throws NotSupportedLinkException, EntityNotFoundException {
        RepoSearchRequest request = buildRepoSearchRequest(linkDto.url());

        RepositoryEventResponse repositoryEventResponse =
            githubClient.fetchLastModified(request.owner(), request.repo());
        if (repositoryEventResponse == null) {
            throw new EntityNotFoundException("Ссылка %s указывает на не существующий репозиторий"
                .formatted(linkDto.url()));
        }
        return defineShouldMakeUpdateRequest(linkDto, repositoryEventResponse.createdAt(), "Репозиторий обновился");
    }

    private RepoSearchRequest buildRepoSearchRequest(URI url) throws NotSupportedLinkException {
        String[] parts = getUrlPathParts(url, SITE_BASE_URL);
        if (parts.length != 2) {
            throw new NotSupportedLinkException(
                "Ссылка должна указывать на репозиторий.\nПример: https://github.com/{owner}/{repo}");
        }
        return new RepoSearchRequest(parts[0], parts[1]);
    }

}
