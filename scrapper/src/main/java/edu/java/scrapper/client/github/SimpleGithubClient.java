package edu.java.scrapper.client.github;

import edu.java.client.ClientConfigRecord;
import edu.java.client.ServiceClient;
import edu.java.general.ApiException;
import edu.java.scrapper.client.dto.github.CommitInfoResponse;
import edu.java.scrapper.client.dto.github.RepositoryEventResponse;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;

public class SimpleGithubClient extends ServiceClient implements GithubClient {
    private static final String PER_PAGE_PARAMETER = "per_page";

    public SimpleGithubClient(String baseUrl) {
        super(baseUrl);
    }

    public SimpleGithubClient(ClientConfigRecord client) {
        super(client);
    }

    @Override public RepositoryEventResponse fetchLastModified(String owner, String repo) {

        Mono<RepositoryEventResponse[]> bodyToMono = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/repos/%s/%s/events".formatted(owner, repo))
                .queryParam(PER_PAGE_PARAMETER, 1)
                .build())
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                response -> response.bodyToMono(ApiException.class)
            ).bodyToMono(RepositoryEventResponse[].class);

        RepositoryEventResponse[] events;
        try {
            events = bodyToMono.retryWhen(retry)
                .block();
        } catch (ApiException e) {
            return null;
        }
        if (events == null) {
            return null;
        }
        return events[0];

    }

    @Override
    public CommitInfoResponse fetchLastModifiedCommit(String owner, String repo) {
        Mono<CommitInfoResponse[]> bodyToMono = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/repos/%s/%s/commits".formatted(owner, repo))
                .queryParam(PER_PAGE_PARAMETER, 1)
                .build())
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                response -> response.bodyToMono(String.class)
                    .flatMap(error -> Mono.error(new ApiException(response.statusCode().value(), error)))
            ).bodyToMono(CommitInfoResponse[].class);

        CommitInfoResponse[] commits;
        try {
            commits = bodyToMono.retryWhen(retry)
                .block();
        } catch (ApiException e) {
            return null;
        }
        if (commits == null) {
            return null;
        }
        return commits[0];
    }
}
