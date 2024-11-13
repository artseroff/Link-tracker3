package edu.java.bot.kafka;

import edu.java.bot.service.kafka.dlq.DeadLetterQueueProducer;
import edu.java.bot.service.link.LinkUpdatesHandler;
import edu.java.request.LinkUpdateRequest;
import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KafkaConsumerTest extends KafkaIntegrationTest {
    private static final String LINK = "https://github.com/artseroff/link-tracker";
    private final KafkaTemplate<String, LinkUpdateRequest> linkUpdateKafkaTemplate;
    private final KafkaTemplate<String, String> stringKafkaTemplate;

    @Value("${kafka.scrapper-topic}")
    private String scrapperTopicName;

    @MockBean
    private DeadLetterQueueProducer deadLetterQueueProducer;

    @MockBean
    private LinkUpdatesHandler linkUpdatesHandler;

    @Autowired
    public KafkaConsumerTest(
        KafkaTemplate<String, LinkUpdateRequest> linkUpdateKafkaTemplate,
        KafkaTemplate<String, String> stringKafkaTemplate
    ) {
        this.linkUpdateKafkaTemplate = linkUpdateKafkaTemplate;
        this.stringKafkaTemplate = stringKafkaTemplate;
    }

    @BeforeAll
    @SneakyThrows
    public void waitMocks() {
        Thread.sleep(1000);
    }

    @Test
    public void botSendUpdatesToChatTest() {

        // Arrange
        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(1L, URI.create(LINK), "", List.of(1L));

        // Act
        linkUpdateKafkaTemplate.send(scrapperTopicName, linkUpdateRequest);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Assert
        Mockito.verify(linkUpdatesHandler).processUpdate(linkUpdateRequest);
    }

    @Test
    public void notValidLinkUpdateRedirectToDlqTest() {

        // Arrange
        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(null, null, null, null);

        // Act
        linkUpdateKafkaTemplate.send(scrapperTopicName, linkUpdateRequest);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Assert
        Mockito.verifyNoInteractions(linkUpdatesHandler);
        Mockito.verify(deadLetterQueueProducer).send(Mockito.any());
    }

    @Test
    public void cantParseJsonToLinkUpdateRedirectToDlqTest() {

        // Arrange
        String noLinkUpdateRequestJson = """
            {
               "message": "Not Found",
               "documentation_url": "https://docs.github.com/rest/repos/repos#get-a-repository"
             }
            """;

        // Act
        stringKafkaTemplate.send(scrapperTopicName, noLinkUpdateRequestJson);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Assert
        Mockito.verifyNoInteractions(linkUpdatesHandler);
        Mockito.verify(deadLetterQueueProducer).send(Mockito.any());
    }

    @Test
    public void anyExceptionDuringSendingToChatRedirectToDlqTest() {

        // Arrange
        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(1L, URI.create(LINK), "", List.of(1L));
        Mockito.doThrow(RuntimeException.class).when(linkUpdatesHandler).processUpdate(Mockito.any());

        // Act
        linkUpdateKafkaTemplate.send(scrapperTopicName, linkUpdateRequest);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Assert
        Mockito.verify(deadLetterQueueProducer).send(Mockito.any());
    }
}
