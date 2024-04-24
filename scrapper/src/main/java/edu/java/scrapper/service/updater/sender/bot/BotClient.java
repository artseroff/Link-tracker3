package edu.java.scrapper.service.updater.sender.bot;

import edu.java.client.ClientConfigRecord;
import edu.java.client.ServiceClient;
import edu.java.general.ApiException;
import edu.java.request.LinkUpdateRequest;
import edu.java.scrapper.service.updater.sender.LinkUpdatesSender;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

public class BotClient extends ServiceClient implements LinkUpdatesSender {

    public BotClient(ClientConfigRecord client) {
        super(client);
    }

    @Override
    public void sendUpdates(LinkUpdateRequest updateRequest) {
        this.webClient
            .post()
            .uri("/update")
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(updateRequest), LinkUpdateRequest.class)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                response -> response.bodyToMono(ApiException.class)
            )
            .bodyToMono(Void.class)
            .retryWhen(retry)
            .block();
    }
}
