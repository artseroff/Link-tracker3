package edu.java.scrapper.service.updater.stackoverflow;

import edu.java.scrapper.client.dto.stackoverflow.QuestionAnswerResponse;
import edu.java.scrapper.client.stackoverflow.StackoverflowClient;
import edu.java.scrapper.service.exception.CorruptedLinkException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import edu.java.scrapper.service.exception.NotSupportedLinkException;
import edu.java.scrapper.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.service.updater.FetchersChainUtils;
import edu.java.scrapper.service.updater.LinkUpdateDescription;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import static edu.java.scrapper.service.updater.FetchersChainUtils.URL_DELIMITER;

@Component
public class StackoverflowUpdatesFetcher extends AbstractUpdatesFetcher {
    private static final String SITE_BASE_URL = "stackoverflow.com";
    private static final String CORRUPTED_LINK_TEXT = """
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
        throws NotSupportedLinkException, EntityNotFoundException, CorruptedLinkException {
        String textPath = getProceedUrlPath(url, SITE_BASE_URL);

        String[] parts = textPath.split(URL_DELIMITER);
        long questionId = fetchQuestionId(parts);
        // Убираем необязательную часть из ссылки
        if (parts.length > 2) {
            List<String> strings = List.of(SITE_BASE_URL, parts[0], parts[1]);
            textPath = String.join(URL_DELIMITER, strings);
        }

        QuestionAnswerResponse questionAnswerResponse = stackoverflowClient.fetchLastModified(questionId);
        if (questionAnswerResponse == null) {
            throw new EntityNotFoundException("Ссылка %s указывает на несуществующий вопрос"
                .formatted(url));
        }

        URI proceedUrl = url;
        if (!textPath.equals(url.getPath())) {
            proceedUrl =
                FetchersChainUtils.createUrl(FetchersChainUtils.SECURE_HYPER_TEXT_PROTOCOL, SITE_BASE_URL, textPath);
        }
        return defineShouldMakeLinkUpdate(
            proceedUrl,
            questionAnswerResponse.lastModified(),
            lastUpdatedAt,
            questionAnswerResponse.getDescription()
        );
    }

    private long fetchQuestionId(String[] parts) throws CorruptedLinkException {

        if (parts.length < 2 || !parts[0].equals("questions")) {
            throw new CorruptedLinkException(CORRUPTED_LINK_TEXT);
        }
        try {
            return Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new CorruptedLinkException(CORRUPTED_LINK_TEXT);
        }
    }

}
