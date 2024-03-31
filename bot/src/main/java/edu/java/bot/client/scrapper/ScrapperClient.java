package edu.java.bot.client.scrapper;

import edu.java.client.ClientConfigRecord;
import edu.java.client.ServiceClient;
import edu.java.general.ApiException;
import edu.java.general.LinkSubscriptionDto;
import edu.java.response.LinkResponse;
import edu.java.response.ListLinksResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class ScrapperClient extends ServiceClient {
    private static final String LINKS = "/links";
    private static final String TG_CHAT = "/tg-chat";

    private static final String ID_PARAMETER = "/{id}";

    public ScrapperClient(ClientConfigRecord client) {
        super(client);
    }

    private Mono<ApiException> buildApiException(ClientResponse response) {
        return response.bodyToMono(ApiException.class);
    }

    public void createChat(long id) {
        this.webClient
            .post()
            .uri(uriBuilder -> uriBuilder.path(TG_CHAT + ID_PARAMETER).build(id))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                this::buildApiException
            )
            .bodyToMono(Void.class)
            .retryWhen(retry)
            .block();
    }

    public void deleteChat(long id) {
        this.webClient
            .delete()
            .uri(uriBuilder -> uriBuilder.path(TG_CHAT + ID_PARAMETER).build(id))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                this::buildApiException
            )
            .bodyToMono(Void.class)
            .retryWhen(retry)
            .block();
    }

    public LinkResponse addLink(LinkSubscriptionDto linkSubscriptionDto) {
        return this.webClient
            .post()
            .uri(LINKS)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(linkSubscriptionDto), LinkSubscriptionDto.class)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                this::buildApiException
            )
            .bodyToMono(LinkResponse.class)
            .retryWhen(retry)
            .block();
    }

    public LinkResponse deleteLink(LinkSubscriptionDto linkSubscriptionDto) {
        return this.webClient
            .method(HttpMethod.DELETE)
            .uri(LINKS)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(linkSubscriptionDto), LinkSubscriptionDto.class)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                this::buildApiException
            )
            .bodyToMono(LinkResponse.class)
            .retryWhen(retry)
            .block();
    }

    public ListLinksResponse getLinks(long id) {
        return this.webClient
            .get()
            .uri(uriBuilder -> uriBuilder.path(LINKS + ID_PARAMETER).build(id))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                this::buildApiException
            )
            .bodyToMono(ListLinksResponse.class)
            .retryWhen(retry)
            .block();
    }
}
