package edu.java.scrapper.service.link;

import edu.java.response.LinkResponse;
import edu.java.scrapper.domain.LinkRepository;
import edu.java.scrapper.domain.SubscriptionRepository;
import edu.java.scrapper.domain.dto.LinkDto;
import edu.java.scrapper.domain.dto.SubscriptionDto;
import edu.java.scrapper.integration.IntegrationTest;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.TgChatService;
import edu.java.scrapper.service.exception.CorruptedLinkException;
import edu.java.scrapper.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import edu.java.scrapper.service.exception.NotSupportedLinkException;
import edu.java.scrapper.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.service.updater.LinkUpdateDescription;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import static org.mockito.Mockito.when;

@Transactional
public abstract class AbstractSimpleLinkServiceTest extends IntegrationTest {

    private static final URI GITHUB_LINK = URI.create("https://github.com/artseroff/Link-tracker");
    private final LinkService linkService;
    private final TgChatService chatService;
    private final LinkRepository linkRepository;
    private final SubscriptionRepository subscriptionRepository;

    @MockBean(name = "headUpdatesFetcher")
    private AbstractUpdatesFetcher updatesFetcher;

    public AbstractSimpleLinkServiceTest(
        LinkService linkService, TgChatService chatService, LinkRepository linkRepository,
        SubscriptionRepository subscriptionRepository
    ) {
        this.linkService = linkService;
        this.chatService = chatService;
        this.linkRepository = linkRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @BeforeEach
    public abstract void truncateTableLinks();

    @ParameterizedTest
    @ValueSource(longs = {1})
    public void track(Long chatId)
        throws CorruptedLinkException, NotSupportedLinkException, EntityNotFoundException, EntityAlreadyExistException {
        // Arrange
        URI inputUrl = GITHUB_LINK;

        long expectedLinkId = 1L;
        SubscriptionDto expectedSubscriptionDto = new SubscriptionDto(chatId, expectedLinkId);

        OffsetDateTime nowTime = OffsetDateTime.now(ZoneOffset.UTC)
            .truncatedTo(ChronoUnit.MINUTES);
        OffsetDateTime yearAgo = nowTime.minusYears(1);

        LinkDto expectedlinkDto = new LinkDto(expectedLinkId, inputUrl, yearAgo, nowTime);

        prepareUpdaterMocks(inputUrl, null, nowTime, yearAgo);

        // Act
        chatService.register(chatId);
        LinkResponse tracked = linkService.track(chatId, inputUrl);

        // Assert
        Assertions.assertEquals(expectedLinkId, tracked.id());
        Assertions.assertEquals(inputUrl, tracked.url());

        Optional<LinkDto> savedLink = linkRepository.findByUrl(inputUrl);
        Assertions.assertTrue(savedLink.isPresent());
        Assertions.assertEquals(expectedlinkDto, savedLink.get());

        Optional<SubscriptionDto> optionalSubscriptionDto = subscriptionRepository.findEntity(expectedSubscriptionDto);
        Assertions.assertTrue(optionalSubscriptionDto.isPresent());
        Assertions.assertEquals(expectedSubscriptionDto, optionalSubscriptionDto.get());
    }

    private void prepareUpdaterMocks(
        URI url,
        OffsetDateTime lastUpdatedAt,
        OffsetDateTime nowTime,
        OffsetDateTime fetchedUpdatedAt
    )
        throws CorruptedLinkException, NotSupportedLinkException, EntityNotFoundException {

        LinkUpdateDescription linkUpdateDescription =
            new LinkUpdateDescription(url, fetchedUpdatedAt, nowTime, "");
        when(updatesFetcher.chainedUpdatesFetching(url, lastUpdatedAt))
            .thenReturn(Optional.of(linkUpdateDescription));
    }

    @Test
    public void track_AlreadyTracked()
        throws CorruptedLinkException, NotSupportedLinkException, EntityNotFoundException, EntityAlreadyExistException {
        // Arrange

        long chatId = 1L;
        URI inputUrl = URI.create("https://github.com/artseroff/Link-tracker");

        // Act && Assert
        track(chatId);
        Assertions.assertThrows(EntityAlreadyExistException.class, () -> linkService.track(chatId, inputUrl));

    }

    @Test
    public void untrack_ExistOneSubscription()
        throws CorruptedLinkException, NotSupportedLinkException, EntityNotFoundException, EntityAlreadyExistException {
        // Arrange
        long chatId = 1L;
        URI inputUrl = GITHUB_LINK;
        long expectedLinkId = 1L;
        SubscriptionDto subscriptionDto = new SubscriptionDto(chatId, expectedLinkId);

        // Act
        track(chatId);
        LinkResponse untracked = linkService.untrack(chatId, inputUrl);

        // Assert
        Assertions.assertEquals(expectedLinkId, untracked.id());
        Assertions.assertEquals(inputUrl, untracked.url());

        Optional<LinkDto> foundLink = linkRepository.findByUrl(inputUrl);
        Assertions.assertTrue(foundLink.isEmpty());

        Optional<SubscriptionDto> optionalSubscriptionDto = subscriptionRepository.findEntity(subscriptionDto);
        Assertions.assertTrue(optionalSubscriptionDto.isEmpty());
    }

    @Test
    public void untrack_ExistTwoSubscriptions()
        throws CorruptedLinkException, NotSupportedLinkException, EntityNotFoundException, EntityAlreadyExistException {
        // Arrange
        long chatId1 = 1L;
        URI inputUrl = GITHUB_LINK;
        long expectedLinkId = 1L;
        SubscriptionDto subscriptionDto = new SubscriptionDto(chatId1, expectedLinkId);

        long chatId2 = 2L;

        // Act
        track(chatId1);
        track(chatId2);
        LinkResponse untracked = linkService.untrack(chatId1, inputUrl);

        // Assert
        Assertions.assertEquals(expectedLinkId, untracked.id());
        Assertions.assertEquals(inputUrl, untracked.url());

        Optional<LinkDto> foundLink = linkRepository.findByUrl(inputUrl);
        Assertions.assertTrue(foundLink.isPresent());

        Optional<SubscriptionDto> optionalSubscriptionDto = subscriptionRepository.findEntity(subscriptionDto);
        Assertions.assertTrue(optionalSubscriptionDto.isEmpty());
    }

    @Test
    public void untrack_NotTrackedLink() {
        // Arrange
        long chatId = 1L;
        URI inputUrl = GITHUB_LINK;

        OffsetDateTime nowTime = OffsetDateTime.now(ZoneOffset.UTC)
            .truncatedTo(ChronoUnit.MINUTES);

        // Act && Assert
        linkRepository.add(inputUrl, nowTime, nowTime);
        Assertions.assertThrows(EntityNotFoundException.class, () -> linkService.untrack(chatId, inputUrl));
    }

    @Test
    public void untrack_NotExistedLink() {
        // Arrange
        long chatId = 1L;

        // Act && Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> linkService.untrack(chatId, GITHUB_LINK));
    }

    @Test
    public void listAll()
        throws CorruptedLinkException, NotSupportedLinkException, EntityNotFoundException, EntityAlreadyExistException {
        // Arrange
        long chatId = 1L;
        URI inputUrl1 = GITHUB_LINK;

        OffsetDateTime nowTime = OffsetDateTime.now(ZoneOffset.UTC)
            .truncatedTo(ChronoUnit.MINUTES);
        OffsetDateTime yearAgo = nowTime.minusYears(1);

        prepareUpdaterMocks(inputUrl1, null, nowTime, yearAgo);
        chatService.register(chatId);
        LinkResponse tracked1 = linkService.track(chatId, inputUrl1);

        List<LinkResponse> expectedLinks = List.of(tracked1);

        // Act
        Collection<LinkResponse> actualLinks = linkService.listAll(chatId);

        // Assert
        Assertions.assertEquals(expectedLinks, actualLinks);
    }

    @Test
    public void noChat() {
        // Arrange
        long chatId = 1L;

        // Act && Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> linkService.track(chatId, GITHUB_LINK));
        Assertions.assertThrows(EntityNotFoundException.class, () -> linkService.untrack(chatId, GITHUB_LINK));
        Assertions.assertThrows(EntityNotFoundException.class, () -> linkService.listAll(chatId));
    }
}
