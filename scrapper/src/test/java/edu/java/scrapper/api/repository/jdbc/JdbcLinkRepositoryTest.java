package edu.java.scrapper.api.repository.jdbc;

import edu.java.scrapper.api.repository.dto.LinkDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class JdbcLinkRepositoryTest {

    private final JdbcLinkRepository linkRepository;

    private final JdbcClient jdbcClient;

    @Autowired
    public JdbcLinkRepositoryTest(JdbcLinkRepository linkRepository, JdbcClient jdbcClient) {
        this.linkRepository = linkRepository;
        this.jdbcClient = jdbcClient;
    }

    @BeforeEach
    public void truncateTableLinks() {
        jdbcClient.sql("truncate table links restart identity CASCADE")
            .update();
    }

    @Test
    void findById_NotExistedTest() {
        // Arrange
        long id = 1L;

        // Act
        Optional<LinkDto> foundLink = linkRepository.findById(id);

        // Assert
        Assertions.assertFalse(foundLink.isPresent());
    }

    @Test
    void findByUrl_NotExistedTest() {
        // Arrange
        URI url = URI.create("https://github.com");

        // Act
        Optional<LinkDto> foundLink = linkRepository.findByUrl(url);

        // Assert
        Assertions.assertFalse(foundLink.isPresent());
    }

    @Test
    void addThenFindByIdAndUrlTest() {
        // Arrange
        long id = 1L;
        URI url = URI.create("https://ya.ru");
        OffsetDateTime time = OffsetDateTime.now(ZoneOffset.UTC)
            .truncatedTo(ChronoUnit.SECONDS);
        LinkDto expectedLink = new LinkDto(id, url, time, time);

        // Act
        LinkDto addedLink = linkRepository.add(url, time, time);
        Optional<LinkDto> foundLinkByUrl = linkRepository.findByUrl(url);
        Optional<LinkDto> foundLinkById = linkRepository.findById(id);

        // Assert
        Assertions.assertEquals(expectedLink, addedLink);

        Assertions.assertTrue(foundLinkById.isPresent());
        Assertions.assertEquals(expectedLink, foundLinkById.get());

        Assertions.assertTrue(foundLinkByUrl.isPresent());
        Assertions.assertEquals(expectedLink, foundLinkByUrl.get());
    }

    @Test
    void addThenRemoveTest() {
        // Arrange
        long id = 1L;
        URI url = URI.create("https://github.com");
        OffsetDateTime time = OffsetDateTime.now(ZoneOffset.UTC)
            .truncatedTo(ChronoUnit.SECONDS);

        // Act
        linkRepository.add(url, time, time);
        linkRepository.remove(id);
        Optional<LinkDto> foundLinkById = linkRepository.findById(id);

        // Assert
        Assertions.assertTrue(foundLinkById.isEmpty());
    }

    @Test
    void remove_NotExistedTest() {
        // Arrange
        long id = 1L;

        // Act & Assert
        Assertions.assertDoesNotThrow(() -> linkRepository.remove(id));
    }

    @Test
    void findAllTest() {
        // Arrange
        long id1 = 1L;
        long id2 = 2L;
        URI githubUrl = URI.create("https://github.com");
        URI stackoverflowUrl = URI.create("https://stackoverflow.com");
        OffsetDateTime time = OffsetDateTime.now(ZoneOffset.UTC)
            .truncatedTo(ChronoUnit.SECONDS);

        LinkDto link1 = new LinkDto(id1, githubUrl, time, time);
        LinkDto link2 = new LinkDto(id2, stackoverflowUrl, time, time);
        List<LinkDto> expectedLinks = List.of(link1, link2);

        // Act
        linkRepository.add(link1.url(), link1.lastUpdatedAt(), link1.lastSchedulerCheck());
        linkRepository.add(link2.url(), link2.lastUpdatedAt(), link2.lastSchedulerCheck());
        Collection<LinkDto> actualLinks = linkRepository.findAll();

        // Assert
        Assertions.assertEquals(expectedLinks, actualLinks);
    }

    @Test
    void findAllBeforeLastSchedulerCheckTest() {
        // Arrange
        long id1 = 1L;
        long id2 = 2L;
        long id3 = 3L;
        long id4 = 4L;
        URI githubUrl1 = URI.create("https://github.com");
        URI githubUrl2 = URI.create("https://github.com/repo");
        URI stackoverflowUrl1 = URI.create("https://stackoverflow.com");
        URI stackoverflowUrl2 = URI.create("https://stackoverflow.com/questions");

        OffsetDateTime currentTime = OffsetDateTime.now(ZoneOffset.UTC)
            .truncatedTo(ChronoUnit.SECONDS);
        OffsetDateTime hourAgoTime = currentTime.minusHours(1);
        OffsetDateTime twoHoursAgoTime = hourAgoTime.minusHours(1);
        OffsetDateTime threeHoursAgoTime = twoHoursAgoTime.minusHours(1);

        LinkDto linkCheckedHourAgo = new LinkDto(id1, githubUrl1, currentTime, hourAgoTime);
        LinkDto linkCheckedTwoHoursAgo = new LinkDto(id2, githubUrl2, currentTime, twoHoursAgoTime);
        LinkDto linkCheckedThreeHoursAgo = new LinkDto(id3, stackoverflowUrl1, currentTime, threeHoursAgoTime);
        LinkDto linkNotChecked = new LinkDto(id4, stackoverflowUrl2, currentTime, null);
        List<LinkDto> expectedLinks = List.of(linkCheckedTwoHoursAgo, linkCheckedThreeHoursAgo, linkNotChecked);

        // Act
        linkRepository.add(
            linkCheckedHourAgo.url(),
            linkCheckedHourAgo.lastUpdatedAt(),
            linkCheckedHourAgo.lastSchedulerCheck()
        );
        linkRepository.add(
            linkCheckedTwoHoursAgo.url(),
            linkCheckedTwoHoursAgo.lastUpdatedAt(),
            linkCheckedTwoHoursAgo.lastSchedulerCheck()
        );
        linkRepository.add(
            linkCheckedThreeHoursAgo.url(),
            linkCheckedThreeHoursAgo.lastUpdatedAt(),
            linkCheckedThreeHoursAgo.lastSchedulerCheck()
        );
        linkRepository.add(linkNotChecked.url(), linkNotChecked.lastUpdatedAt(), linkNotChecked.lastSchedulerCheck());

        Collection<LinkDto> actualLinks = linkRepository.findAllBeforeLastSchedulerCheck(twoHoursAgoTime);

        // Assert
        Assertions.assertEquals(expectedLinks, actualLinks);
    }

}
