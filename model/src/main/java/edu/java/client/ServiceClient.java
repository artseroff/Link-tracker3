package edu.java.client;

import edu.java.client.retry.RetryConfigRecord;
import edu.java.client.retry.WebClientRetryUtils;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

@AllArgsConstructor
public class ServiceClient {

    protected final WebClient webClient;

    protected final Retry retry;

    public ServiceClient(String baseUrl, RetryConfigRecord retryConfigRecord) {
        this(
            buildWebClient(baseUrl),
            WebClientRetryUtils.buildRetry(retryConfigRecord)
        );
    }

    public ServiceClient(ClientConfigRecord client) {
        this(client.baseUrl(), client.retry());
    }

    public ServiceClient(String baseUrl) {
        this(baseUrl, null);
    }

    private static WebClient buildWebClient(@NotNull String baseUrl) {
        if (baseUrl.isBlank()) {
            throw new IllegalArgumentException("Передан пустой base URL");
        }
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}
