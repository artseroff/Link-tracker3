package edu.java.scrapper.service.link;

import edu.java.response.LinkResponse;
import edu.java.scrapper.configuration.db.AccessType;
import edu.java.scrapper.domain.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.jpa.entity.ChatEntity;
import edu.java.scrapper.domain.jpa.entity.LinkEntity;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import static org.mockito.Mockito.when;

@SpringBootTest
public class JpaLinkServiceTest extends IntegrationTest {
    private static final URI GITHUB_LINK = URI.create("https://github.com/artseroff/Link-tracker");
    private final LinkService linkService;
    private final TgChatService chatService;
    private final JpaLinkRepository linkRepository;

    @MockBean(name = "headUpdatesFetcher")
    private AbstractUpdatesFetcher updatesFetcher;

    @Autowired
    public JpaLinkServiceTest(
        LinkService linkService, TgChatService chatService, JpaLinkRepository linkRepository
    ) {
        this.linkService = linkService;
        this.chatService = chatService;
        this.linkRepository = linkRepository;
    }

    @DynamicPropertySource
    static void setAccessType(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> AccessType.JPA);
    }

    @BeforeEach
    public void truncateTableLinks() {
        linkRepository.truncateTable();
    }

    @Test
    @Transactional
    public void track()
        throws CorruptedLinkException, NotSupportedLinkException, EntityNotFoundException, EntityAlreadyExistException {
        // Arrange
        trackChat(1L, true);

    }

    public void trackChat(Long chatId, boolean needAssert)
        throws CorruptedLinkException, NotSupportedLinkException, EntityNotFoundException, EntityAlreadyExistException {
        // Arrange
        URI inputUrl = GITHUB_LINK;

        long expectedLinkId = 1L;

        OffsetDateTime nowTime = OffsetDateTime.now(ZoneOffset.UTC)
            .truncatedTo(ChronoUnit.MINUTES);
        OffsetDateTime yearAgo = nowTime.minusYears(1);

        ChatEntity chatEntity = new ChatEntity(chatId, new LinkedHashSet<>());

        LinkEntity expectedLinkEntity = new LinkEntity(expectedLinkId, inputUrl, yearAgo, nowTime, Set.of(chatEntity));

        prepareUpdaterMocks(inputUrl, null, nowTime, yearAgo);

        // Act
        chatService.register(chatId);
        LinkResponse tracked = linkService.track(chatId, inputUrl);

        // Assert
        if (!needAssert) {
            return;
        }

        Assertions.assertEquals(expectedLinkId, tracked.id());
        Assertions.assertEquals(inputUrl, tracked.url());

        Optional<LinkEntity> optionalLinkEntity = linkRepository.findByUrl(inputUrl);
        Assertions.assertTrue(optionalLinkEntity.isPresent());

        LinkEntity actualLinkEntity = optionalLinkEntity.get();

        Assertions.assertEquals(expectedLinkEntity, actualLinkEntity);

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

    @Transactional
    @Test
    public void track_AlreadyTracked()
        throws CorruptedLinkException, NotSupportedLinkException, EntityNotFoundException, EntityAlreadyExistException {
        // Arrange

        long chatId = 1L;
        URI inputUrl = URI.create("https://github.com/artseroff/Link-tracker");

        // Act && Assert
        trackChat(chatId, false);
        Assertions.assertThrows(EntityAlreadyExistException.class, () -> linkService.track(chatId, inputUrl));

    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Test
    public void untrack_ExistOneSubscription()
        throws CorruptedLinkException, NotSupportedLinkException, EntityNotFoundException, EntityAlreadyExistException {
        // Arrange
        long chatId = 1L;
        URI inputUrl = GITHUB_LINK;
        long expectedLinkId = 1L;

        // Act
        trackChat(chatId, false);
        LinkResponse untracked = linkService.untrack(chatId, inputUrl);

        // Assert
        Assertions.assertEquals(expectedLinkId, untracked.id());
        Assertions.assertEquals(inputUrl, untracked.url());

        Optional<LinkEntity> foundLink = linkRepository.findByUrl(inputUrl);
        Assertions.assertTrue(foundLink.isEmpty());
    }

    @Transactional
    @Test
    public void untrack_ExistTwoSubscriptions()
        throws CorruptedLinkException, NotSupportedLinkException, EntityNotFoundException, EntityAlreadyExistException {
        // Arrange
        long chatId1 = 1L;
        URI inputUrl = GITHUB_LINK;
        long expectedLinkId = 1L;

        long chatId2 = 2L;

        // Act
        trackChat(chatId1, false);
        trackChat(chatId2, false);
        LinkResponse untracked = linkService.untrack(chatId1, inputUrl);

        // Assert
        Assertions.assertEquals(expectedLinkId, untracked.id());
        Assertions.assertEquals(inputUrl, untracked.url());

        Optional<LinkEntity> foundLink = linkRepository.findByUrl(inputUrl);
        Assertions.assertTrue(foundLink.isPresent());

        Assertions.assertFalse(foundLink.get().getChats().contains(new ChatEntity(chatId1, new LinkedHashSet<>())));
    }

    @Transactional
    @Test
    public void untrack_NotTrackedLink() {
        // Arrange
        long chatId = 1L;
        URI inputUrl = GITHUB_LINK;

        OffsetDateTime nowTime = OffsetDateTime.now(ZoneOffset.UTC)
            .truncatedTo(ChronoUnit.MINUTES);
        LinkEntity linkEntity = new LinkEntity(1L, inputUrl, nowTime, nowTime, new LinkedHashSet<>());

        // Act && Assert
        linkRepository.save(linkEntity);
        Assertions.assertThrows(EntityNotFoundException.class, () -> linkService.untrack(chatId, inputUrl));
    }

    @Transactional

    @Test
    public void untrack_NotExistedLink() {
        // Arrange
        long chatId = 1L;

        // Act && Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> linkService.untrack(chatId, GITHUB_LINK));
    }

    @Transactional
    @Test
    public void listAll()
        throws CorruptedLinkException, NotSupportedLinkException, EntityNotFoundException, EntityAlreadyExistException {
        // Arrange
        long chatId = 1L;
        track();
        List<LinkResponse> expectedLinks = List.of(new LinkResponse(1L, GITHUB_LINK));

        // Act
        Collection<LinkResponse> actualLinks = linkService.listAll(chatId);

        // Assert
        Assertions.assertEquals(expectedLinks, actualLinks);
    }

    @Transactional
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
