package edu.java.scrapper.domain;

import edu.java.scrapper.domain.dto.ChatDto;
import edu.java.scrapper.domain.dto.LinkDto;
import edu.java.scrapper.domain.dto.SubscriptionDto;
import edu.java.scrapper.integration.IntegrationTest;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class AbstractSubscriptionRepositoryTest extends IntegrationTest {

    private final SubscriptionRepository subscriptionRepository;
    private final LinkRepository linkRepository;
    private final TgChatRepository chatRepository;

    protected AbstractSubscriptionRepositoryTest(
        SubscriptionRepository subscriptionRepository,
        LinkRepository linkRepository,
        TgChatRepository chatRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.linkRepository = linkRepository;
        this.chatRepository = chatRepository;
    }

    @BeforeEach
    public abstract void truncateTableLinks();

    @Test
    void addThenFindTest() {
        // Arrange
        long id = 1L;
        SubscriptionDto expectedSub = new SubscriptionDto(id, id);

        // Act
        chatRepository.add(id);
        linkRepository.add(URI.create("https://github.com"), null, null);
        SubscriptionDto addedSub = subscriptionRepository.add(expectedSub);
        Optional<SubscriptionDto> foundSub = subscriptionRepository.findEntity(expectedSub);

        // Assert
        Assertions.assertEquals(expectedSub, addedSub);
        Assertions.assertTrue(foundSub.isPresent());
        Assertions.assertEquals(expectedSub, foundSub.get());

    }

    @Test
    void addThenRemoveTest() {
        // Arrange
        long id = 1L;
        SubscriptionDto expectedSub = new SubscriptionDto(id, id);

        // Act
        chatRepository.add(id);
        linkRepository.add(URI.create("https://github.com"), null, null);
        SubscriptionDto addedSub = subscriptionRepository.add(expectedSub);
        subscriptionRepository.remove(addedSub);
        Optional<SubscriptionDto> foundSub = subscriptionRepository.findEntity(expectedSub);

        // Assert
        Assertions.assertTrue(foundSub.isEmpty());
    }

    @Test
    void remove_NotExistedTest() {
        // Arrange
        long id = 1L;

        // Act & Assert
        Assertions.assertDoesNotThrow(() -> subscriptionRepository.remove(new SubscriptionDto(id, id)));
    }

    @Test
    void findAllTest() {
        // Arrange
        long id = 1L;
        SubscriptionDto sub = new SubscriptionDto(id, id);
        List<SubscriptionDto> expectedSubs = List.of(sub);

        // Act
        chatRepository.add(id);
        linkRepository.add(URI.create("https://github.com"), null, null);
        subscriptionRepository.add(sub);
        Collection<SubscriptionDto> actualSubs = subscriptionRepository.findAll();

        // Assert
        Assertions.assertEquals(expectedSubs, actualSubs);
    }

    @Test
    void findEntity_NotExistedTest() {
        // Arrange
        long id = 1L;

        // Act
        Optional<SubscriptionDto> entity = subscriptionRepository.findEntity(new SubscriptionDto(id, id));

        // Assert
        Assertions.assertFalse(entity.isPresent());
    }

    @Test
    void findChatsByLinkId() {
        // Arrange
        long id1 = 1L;
        long id2 = 2L;
        List<ChatDto> expectedChats = List.of(new ChatDto(id1), new ChatDto(id2));

        // Act
        chatRepository.add(id1);
        chatRepository.add(id2);
        linkRepository.add(URI.create("https://github.com"), null, null);

        SubscriptionDto firstSub = new SubscriptionDto(id1, id1);
        SubscriptionDto secondSub = new SubscriptionDto(id2, id1);

        subscriptionRepository.add(firstSub);
        subscriptionRepository.add(secondSub);
        Collection<ChatDto> actualChats = subscriptionRepository.findChatsByLinkId(id1);

        // Assert
        Assertions.assertEquals(expectedChats, actualChats);
    }

    @Test
    void findLinksByChatId() {
        // Arrange
        long id1 = 1L;
        long id2 = 2L;
        LinkDto link1 = new LinkDto(id1, URI.create("https://github.com"), null, null);
        LinkDto link2 = new LinkDto(id2, URI.create("https://github.com/repo"), null, null);
        List<LinkDto> expectedLinks = List.of(link1, link2);

        // Act
        chatRepository.add(id1);
        linkRepository.add(link1.url(), link1.lastUpdatedAt(), link1.lastSchedulerCheck());
        linkRepository.add(link2.url(), link2.lastUpdatedAt(), link2.lastSchedulerCheck());

        SubscriptionDto subFirstLink = new SubscriptionDto(id1, id1);
        SubscriptionDto subSecondLink = new SubscriptionDto(id1, id2);

        subscriptionRepository.add(subFirstLink);
        subscriptionRepository.add(subSecondLink);
        Collection<LinkDto> actualLinks = subscriptionRepository.findLinksByChatId(id1);

        // Assert
        Assertions.assertEquals(expectedLinks, actualLinks);
    }

}
