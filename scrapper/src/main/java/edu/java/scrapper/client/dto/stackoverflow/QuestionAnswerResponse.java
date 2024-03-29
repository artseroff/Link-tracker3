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

    @JsonProperty("last_edit_date")
    OffsetDateTime lastEditDate,

    Owner owner
) {
    public String getDescription() {
        if (lastEditDate != null) {
            return "Ответ пользователя %s был отредактирован".formatted(owner.name);
        }
        return "Пользователь %s добавил новый ответ".formatted(owner.name);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Owner(
        @JsonProperty("display_name")
        String name
    ) {
    }

}
