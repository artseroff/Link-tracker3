package edu.java.bot.service.kafka;

import edu.java.bot.service.kafka.dlq.DeadLetterQueue;
import edu.java.bot.service.link.LinkUpdatesHandler;
import edu.java.request.LinkUpdateRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScrapperMessagesListener {
    private final LinkUpdatesHandler linkUpdatesHandler;
    private final Validator validator;
    private final DeadLetterQueue deadLetterQueue;

    @KafkaListener(topics = "${kafka.scrapper-topic}",
                   containerFactory = "linkUpdateKafkaListenerContainerFactory",
                   concurrency = "1")
    public void listenMessages(LinkUpdateRequest updateRequest) {
        Optional<String> constraints = validateLinkUpdateRequest(updateRequest);
        if (constraints.isPresent()) {
            deadLetterQueue.send(constraints.get());
            return;
        }
        try {
            linkUpdatesHandler.processUpdate(updateRequest);
        } catch (Exception e) {
            deadLetterQueue.send(e.getMessage());
        }
    }

    private Optional<String> validateLinkUpdateRequest(LinkUpdateRequest updateRequest) {
        Set<ConstraintViolation<LinkUpdateRequest>> violations = validator.validate(updateRequest);
        if (violations.isEmpty()) {
            return Optional.empty();
        }
        StringBuilder sb = new StringBuilder("Ошибки валидации:\n");
        for (ConstraintViolation<LinkUpdateRequest> constraintViolation : violations) {
            sb.append(constraintViolation.getMessage());
        }
        return Optional.of(sb.toString());
    }
}
