package edu.java.scrapper.client.bot;

import edu.java.general.ApiException;
import edu.java.request.LinkUpdateRequest;
import edu.java.scrapper.client.AbstractClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class BotClient extends AbstractClient {
    public BotClient(String baseUrl) {
        super(baseUrl);
    }

    public void updates(LinkUpdateRequest request) {
        this.webClient
            .post()
            .uri("/update")
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), LinkUpdateRequest.class)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                this::buildApiException
            )
            .bodyToMono(Void.class)
            .block();
    }

    private Mono<ApiException> buildApiException(ClientResponse response) {
        HttpStatus httpStatus = (HttpStatus) response.statusCode();

        return response
            .bodyToMono(String.class)
            .flatMap(errorBody -> Mono.error(new ApiException(
                httpStatus.value(),
                errorBody
            )));
    }
}
