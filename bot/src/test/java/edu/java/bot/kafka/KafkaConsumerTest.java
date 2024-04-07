package edu.java.bot.kafka;

import edu.java.bot.service.link.LinkUpdatesHandler;
import edu.java.request.LinkUpdateRequest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
public class KafkaConsumerTest extends KafkaIntegrationTest {
    @MockBean
    private LinkUpdatesHandler linkUpdatesHandler;

    private final KafkaTemplate<String, LinkUpdateRequest> linkUpdateKafkaTemplate;

    @Autowired
    public KafkaConsumerTest(KafkaTemplate<String, LinkUpdateRequest> linkUpdateKafkaTemplate) {
        this.linkUpdateKafkaTemplate = linkUpdateKafkaTemplate;
    }

    @Test
    public void botSendUpdatesToChatTest(@Value("${kafka.scrapper-topic}") String scrapperTopicName) {

        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(1L, URI.create("https://github.com/artseroff/Link-tracker"), "", List.of(1L));

        linkUpdateKafkaTemplate.send(scrapperTopicName, linkUpdateRequest);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Mockito.verify(linkUpdatesHandler).processUpdate(linkUpdateRequest);
    }
}
