package edu.java.scrapper.api.service.updater.stackoverflow;

import edu.java.request.LinkUpdateRequest;
import edu.java.scrapper.api.repository.LinkRepository;
import edu.java.scrapper.api.repository.SubscriptionRepository;
import edu.java.scrapper.api.repository.dto.LinkDto;
import edu.java.scrapper.api.service.exception.EntityNotFoundException;
import edu.java.scrapper.api.service.exception.NotSupportedLinkException;
import edu.java.scrapper.api.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.client.dto.stackoverflow.QuestionAnswerResponse;
import edu.java.scrapper.client.stackoverflow.StackoverflowClient;
import java.net.URI;
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
        SubscriptionRepository subscriptionRepository,
        LinkRepository linkRepository,
        StackoverflowClient stackoverflowClient
    ) {
        super(subscriptionRepository, linkRepository);
        this.stackoverflowClient = stackoverflowClient;
    }

    @Override
    public Optional<LinkUpdateRequest> fetchUpdatesFromLink(LinkDto linkDto)
        throws NotSupportedLinkException, EntityNotFoundException {
        long questionId = fetchQuestionId(linkDto.url());

        QuestionAnswerResponse questionAnswerResponse = stackoverflowClient.fetchLastModified(questionId);
        if (questionAnswerResponse == null) {
            throw new EntityNotFoundException("Ссылка %s указывает на несуществующий вопрос"
                .formatted(linkDto.url()));
        }
        return makeLinkUpdate(
            linkDto,
            questionAnswerResponse.lastModified(),
            "В вопросе появились обновления"
        );
    }

    private long fetchQuestionId(URI url) throws NotSupportedLinkException {
        String[] parts = getUrlPathParts(url, SITE_BASE_URL);
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
