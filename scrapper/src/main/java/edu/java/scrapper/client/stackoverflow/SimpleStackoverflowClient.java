package edu.java.scrapper.client.stackoverflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.java.client.ClientConfigRecord;
import edu.java.client.ServiceClient;
import edu.java.general.ApiException;
import edu.java.scrapper.client.dto.stackoverflow.QuestionAnswerResponse;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;

public class SimpleStackoverflowClient extends ServiceClient implements StackoverflowClient {
    private final static ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public SimpleStackoverflowClient(String baseUrl) {
        super(baseUrl);
    }

    public SimpleStackoverflowClient(ClientConfigRecord client) {
        super(client);
    }

    @Override
    public QuestionAnswerResponse fetchLastModified(long questionId) {
        Mono<JsonNode> jsonNodeMono = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/questions/%d/answers".formatted(questionId))
                .queryParam("pagesize", 1)
                .queryParam("site", "stackoverflow")
                .queryParam("sort", "activity")
                .queryParam("order", "desc")
                .build())
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                response -> response.bodyToMono(ApiException.class)
            )
            .bodyToMono(JsonNode.class);
        try {
            JsonNode root = jsonNodeMono.retryWhen(retry)
                .block();

            JsonNode items = root.get("items");
            JsonNode lastModified = items.get(0);

            return OBJECT_MAPPER.treeToValue(lastModified, QuestionAnswerResponse.class);
        } catch (ApiException | NullPointerException | JsonProcessingException e) {
            return null;
        }
    }
}
