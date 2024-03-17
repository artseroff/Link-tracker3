package edu.java.scrapper.api.service.updater.stackoverflow;

import edu.java.scrapper.api.service.exception.EntityNotFoundException;
import edu.java.scrapper.api.service.exception.NotSupportedLinkException;
import edu.java.scrapper.api.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.api.service.updater.LinkUpdateDescription;
import edu.java.scrapper.client.dto.stackoverflow.QuestionAnswerResponse;
import edu.java.scrapper.client.stackoverflow.StackoverflowClient;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class StackoverflowUpdatesFetcher extends AbstractUpdatesFetcher {
    private static final String SITE_BASE_URL = "stackoverflow.com";
    private static final String NOT_SUPPORTED_TEXT = """
          Ссылка должна указывать на вопрос. Пример:
          1) https://stackoverflow.com/questions/{questionId}
          2) https://stackoverflow.com/questions/{questionId}/{additional}...
        """;
    private final StackoverflowClient stackoverflowClient;

    public StackoverflowUpdatesFetcher(
        StackoverflowClient stackoverflowClient
    ) {
        this.stackoverflowClient = stackoverflowClient;
    }

    @Override
    public Optional<LinkUpdateDescription> fetchUpdatesFromLink(URI url, OffsetDateTime lastUpdatedAt)
        throws NotSupportedLinkException, EntityNotFoundException {
        String textPath = getProceedUrl(url, SITE_BASE_URL);

        String[] parts = textPath.split(URL_DELIMITER);
        long questionId = fetchQuestionId(parts);
        // Убираем необязательную часть из ссылки
        if (parts.length > 2) {
            textPath = parts[0] + URL_DELIMITER + parts[1];
        }

        QuestionAnswerResponse questionAnswerResponse = stackoverflowClient.fetchLastModified(questionId);
        if (questionAnswerResponse == null) {
            throw new EntityNotFoundException("Ссылка %s указывает на несуществующий вопрос"
                .formatted(url));
        }
        return defineShouldMakeLinkUpdate(
            textPath,
            questionAnswerResponse.lastModified(),
            lastUpdatedAt,
            "В вопросе появились обновления"
        );
    }

    private long fetchQuestionId(String[] parts) throws NotSupportedLinkException {

        if (parts.length < 2) {
            throw new NotSupportedLinkException(NOT_SUPPORTED_TEXT);
        }
        try {
            return Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new NotSupportedLinkException(NOT_SUPPORTED_TEXT);
        }
    }

}
