package edu.java.scrapper.client.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommitInfoResponse(
    Author author,
    CommitNode commit
) {
    public OffsetDateTime getLastModifiedDate() {
        return commit.author.date;
    }

    public String getDescription() {
        return "Пользователь %s добавил новый коммит с сообщением \"%s\"".formatted(author.login, commit.message);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Author(
        String login
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CommitNode(
        CommitNodeAuthor author,

        String message
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CommitNodeAuthor(
        OffsetDateTime date
    ) {
    }
}
