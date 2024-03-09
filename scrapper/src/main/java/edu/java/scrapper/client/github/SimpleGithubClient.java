package edu.java.scrapper.client.github;

import edu.java.scrapper.client.AbstractClient;
import edu.java.scrapper.client.WebClientRuntimeException;
import edu.java.scrapper.dto.github.RepositoryEventResponse;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;

public class SimpleGithubClient extends AbstractClient implements GithubClient {

    public SimpleGithubClient(String baseUrl) {
        super(baseUrl);
    }

    @Override public RepositoryEventResponse fetchLastModified(String owner, String repo) {

        Mono<RepositoryEventResponse[]> bodyToMono = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/repos/%s/%s/events".formatted(owner, repo))
                .queryParam("per_page", 1)
                .build())
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                response -> response.bodyToMono(String.class)
                    .flatMap(error -> Mono.error(new WebClientRuntimeException(error)))
            ).bodyToMono(RepositoryEventResponse[].class);

        RepositoryEventResponse[] events;
        try {
            events = bodyToMono.block();
        } catch (WebClientRuntimeException e) {
            return null;
        }
        if (events == null) {
            return null;
        }
        return events[0];

    }
}
