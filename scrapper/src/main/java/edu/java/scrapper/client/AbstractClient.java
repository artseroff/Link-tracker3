package edu.java.scrapper.client;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.reactive.function.client.WebClient;

public abstract class AbstractClient {
    protected final WebClient webClient;

    public AbstractClient(String baseUrl) {
        this.webClient = buildWebClient(baseUrl);
    }

    private WebClient buildWebClient(@NotNull String baseUrl) {
        if (baseUrl.isBlank()) {
            throw new IllegalArgumentException("Передан пустой base URL");
        }
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}
