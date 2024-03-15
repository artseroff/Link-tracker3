package edu.java.scrapper.client.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RepositoryEventResponse(
    long id,
    String type,
    @JsonProperty("created_at")
    OffsetDateTime createdAt,

    Actor actor,
    Repo repo
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Actor(
        @JsonProperty("display_login")
        String name
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Repo(
        String name
    ) {
    }
}
