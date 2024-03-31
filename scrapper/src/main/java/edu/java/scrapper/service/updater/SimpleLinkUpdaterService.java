package edu.java.scrapper.service.updater;

import edu.java.request.LinkUpdateRequest;
import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.LinkRepository;
import edu.java.scrapper.domain.SubscriptionRepository;
import edu.java.scrapper.domain.dto.ChatDto;
import edu.java.scrapper.domain.dto.LinkDto;
import edu.java.scrapper.service.exception.CorruptedLinkException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import edu.java.scrapper.service.exception.NotSupportedLinkException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public class SimpleLinkUpdaterService implements LinkUpdaterService {
    private final LinkRepository linkRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ApplicationConfig config;
    private final BotClient botClient;
    private final AbstractUpdatesFetcher headUpdatesFetcher;

    public SimpleLinkUpdaterService(
        LinkRepository linkRepository,
        SubscriptionRepository subscriptionRepository,
        ApplicationConfig config,
        BotClient botClient,
        AbstractUpdatesFetcher headUpdatesFetcher
    ) {
        this.linkRepository = linkRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.config = config;
        this.botClient = botClient;
        this.headUpdatesFetcher = headUpdatesFetcher;
    }

    @Override
    @Transactional
    public int update() {
        Duration delay = config.scheduler().forceCheckDelay();
        OffsetDateTime limitTime = OffsetDateTime.now(ZoneOffset.UTC).minus(delay);

        long linksLimit = config.scheduler().scanLinksLimit();
        Collection<LinkDto> allBeforeLastSchedulerCheck =
            linkRepository.findAllBeforeLastSchedulerCheck(limitTime, linksLimit);

        int countUpdates = 0;
        for (LinkDto linkDto : allBeforeLastSchedulerCheck) {

            Optional<LinkUpdateDescription> updateDescriptionOptional;

            try {
                updateDescriptionOptional =
                    headUpdatesFetcher.chainedUpdatesFetching(linkDto.url(), linkDto.lastUpdatedAt());
                if (updateDescriptionOptional.isPresent()) {
                    countUpdates++;
                    processValidLink(linkDto.id(), updateDescriptionOptional.get());
                }
            } catch (NotSupportedLinkException | EntityNotFoundException | CorruptedLinkException e) {
                processInvalidLink(linkDto, e.getMessage());
            }
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
                "Ссылка %s будет удалена из отслеживаемых. Причина:\n%s".formatted(linkDto.url(), errorMessage),
                chatIds
            );
        botClient.updates(notUpdateRequest);
        // Каскадное удаление
        linkRepository.remove(linkDto.id());
    }

    private void processValidLink(long linkId, LinkUpdateDescription linkUpdateDescription) {
        OffsetDateTime fetchedUpdateDate = linkUpdateDescription.lastUpdatedAt();
        OffsetDateTime checkedTime = linkUpdateDescription.lastSchedulerCheck();
        linkRepository.updateModifiedAndSchedulerCheckDates(linkId, fetchedUpdateDate, checkedTime);

        List<Long> chatIds = subscriptionRepository.findChatsByLinkId(linkId).stream()
            .map(ChatDto::id)
            .toList();

        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(linkId, linkUpdateDescription.url(), linkUpdateDescription.description(), chatIds);
        botClient.updates(linkUpdateRequest);
    }
}
