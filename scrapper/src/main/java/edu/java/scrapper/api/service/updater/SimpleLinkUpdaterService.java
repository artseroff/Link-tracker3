package edu.java.scrapper.api.service.updater;

import edu.java.request.LinkUpdateRequest;
import edu.java.scrapper.api.repository.LinkRepository;
import edu.java.scrapper.api.repository.SubscriptionRepository;
import edu.java.scrapper.api.repository.dto.ChatDto;
import edu.java.scrapper.api.repository.dto.LinkDto;
import edu.java.scrapper.api.service.exception.EntityNotFoundException;
import edu.java.scrapper.api.service.exception.NotSupportedLinkException;
import edu.java.scrapper.api.service.updater.github.GithubUpdatesFetcher;
import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SimpleLinkUpdaterService implements LinkUpdaterService {
    private final LinkRepository linkRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ApplicationConfig config;
    private final BotClient botClient;
    private final GithubUpdatesFetcher githubUpdatesFetcher;

    public SimpleLinkUpdaterService(
        LinkRepository linkRepository,
        SubscriptionRepository subscriptionRepository, ApplicationConfig config, BotClient botClient,
        GithubUpdatesFetcher githubUpdatesFetcher
    ) {
        this.linkRepository = linkRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.config = config;
        this.botClient = botClient;
        this.githubUpdatesFetcher = githubUpdatesFetcher;
    }

    @Override
    @Transactional
    public int update() {
        Duration delay = config.scheduler().forceCheckDelay();

        OffsetDateTime limitTime = OffsetDateTime.now().minus(delay);

        long linksLimit = config.scheduler().scanLinksLimit();
        Collection<LinkDto> allBeforeLastSchedulerCheck =
            linkRepository.findAllBeforeLastSchedulerCheck(limitTime, linksLimit);

        int countUpdates = 0;
        for (LinkDto linkDto : allBeforeLastSchedulerCheck) {

            Optional<LinkUpdateRequest> requestOptional;
            try {
                try {
                    requestOptional = githubUpdatesFetcher.fetchUpdatesFromLink(linkDto);
                } catch (NotSupportedLinkException e) {
                    // TODO stackoverflow
                    requestOptional = githubUpdatesFetcher.fetchUpdatesFromLink(linkDto);
                }
            } catch (NotSupportedLinkException | EntityNotFoundException e) {
                processInvalidLink(linkDto, e.getMessage());
                continue;
            }

            if (requestOptional.isPresent()) {
                countUpdates++;
                botClient.updates(requestOptional.get());
            }

            /*OffsetDateTime fetchedUpdateDate = null;
            if (linkDto.lastUpdatedAt().isBefore(fetchedUpdateDate)) {
                List<Long> chatIds = subscriptionRepository.findChatsByLinkId(linkDto.id()).stream()
                    .map(ChatDto::id)
                    .toList();

                LinkUpdateRequest linkUpdateRequest =
                    new LinkUpdateRequest(linkDto.id(), linkDto.url(), "Информация обновилась", chatIds);
                botClient.updates(linkUpdateRequest);
            }*/

        }
        return countUpdates;
    }

    private void processInvalidLink(LinkDto linkDto, String errorMessage) {
        List<Long> chatIds = subscriptionRepository.findChatsByLinkId(linkDto.id()).stream()
            .map(ChatDto::id)
            .toList();
        LinkUpdateRequest notUpdateRequest =
            new LinkUpdateRequest(
                linkDto.id(),
                linkDto.url(),
                "Ссылка будет удалена из отслеживаемых. Причина:\n" + errorMessage,
                chatIds
            );
        botClient.updates(notUpdateRequest);
        // Каскадное удаление
        linkRepository.remove(linkDto.id());
    }
}
