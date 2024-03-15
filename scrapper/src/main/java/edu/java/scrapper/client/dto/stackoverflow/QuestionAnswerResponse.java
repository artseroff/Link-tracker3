package edu.java.scrapper.client.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestionAnswerResponse(
    @JsonProperty("question_id")
    long questionId,
    @JsonProperty("answer_id")
    long answerId,
    @JsonProperty("last_activity_date")
    OffsetDateTime lastModified,

    Owner owner
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Owner(
        @JsonProperty("display_name")
        String name
    ) {
    }
}
