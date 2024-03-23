package edu.java.scrapper.service.updater.jpa;

import edu.java.request.LinkUpdateRequest;
import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.jpa.entity.ChatEntity;
import edu.java.scrapper.domain.jpa.entity.LinkEntity;
import edu.java.scrapper.service.exception.CorruptedLinkException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import edu.java.scrapper.service.exception.NotSupportedLinkException;
import edu.java.scrapper.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.service.updater.LinkUpdateDescription;
import edu.java.scrapper.service.updater.LinkUpdaterService;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaLinkUpdaterService implements LinkUpdaterService {
    private final JpaLinkRepository linkRepository;
    private final ApplicationConfig config;
    private final BotClient botClient;
    private final AbstractUpdatesFetcher headUpdatesFetcher;

    public JpaLinkUpdaterService(
        JpaLinkRepository linkRepository,
        ApplicationConfig config,
        BotClient botClient,
        @Qualifier("headUpdatesFetcher") AbstractUpdatesFetcher headUpdatesFetcher
    ) {
        this.linkRepository = linkRepository;
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
        Collection<LinkEntity> allBeforeLastSchedulerCheck =
            linkRepository.findAllBeforeLastSchedulerCheck(limitTime, linksLimit);

        int countUpdates = 0;
        for (LinkEntity linkEntity : allBeforeLastSchedulerCheck) {

            Optional<LinkUpdateDescription> updateDescriptionOptional;

            try {
                updateDescriptionOptional =
                    headUpdatesFetcher.chainedUpdatesFetching(
                        URI.create(linkEntity.getUrl()),
                        linkEntity.getLastUpdatedAt()
                    );

                if (updateDescriptionOptional.isPresent()) {
                    countUpdates++;
                    processValidLink(linkEntity, updateDescriptionOptional.get());
                }

            } catch (NotSupportedLinkException | EntityNotFoundException | CorruptedLinkException e) {
                processInvalidLink(linkEntity, e.getMessage());
            }

        }
        return countUpdates;
    }

    private void processInvalidLink(LinkEntity linkEntity, String errorMessage) {
        List<Long> chatIds = linkEntity.getChats().stream()
            .map(ChatEntity::getId)
            .toList();
        LinkUpdateRequest notUpdateRequest =
            new LinkUpdateRequest(
                linkEntity.getId(),
                URI.create(linkEntity.getUrl()),
                "Ссылка %s будет удалена из отслеживаемых. Причина:\n%s".formatted(linkEntity.getUrl(), errorMessage),
                chatIds
            );
        botClient.updates(notUpdateRequest);
        // Каскадное удаление
        linkRepository.delete(linkEntity);
    }

    private void processValidLink(LinkEntity linkEntity, LinkUpdateDescription linkUpdateDescription) {
        OffsetDateTime fetchedUpdateDate = linkUpdateDescription.lastUpdatedAt();
        OffsetDateTime checkedTime = linkUpdateDescription.lastSchedulerCheck();
        linkEntity.setLastUpdatedAt(fetchedUpdateDate);
        linkEntity.setLastSchedulerCheck(checkedTime);

        List<Long> chatIds = linkEntity.getChats().stream()
            .map(ChatEntity::getId)
            .toList();

        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(
                linkEntity.getId(),
                linkUpdateDescription.url(),
                linkUpdateDescription.description(),
                chatIds
            );
        botClient.updates(linkUpdateRequest);
    }

}
