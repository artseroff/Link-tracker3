package edu.java.bot.service.link;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.BotController;
import edu.java.request.LinkUpdateRequest;
import io.micrometer.core.instrument.Counter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdatesHandler {
    private final BotController botController;
    private final Counter proceedMessagesCounter;

    public void processUpdate(LinkUpdateRequest request) {
        List<Long> chatIds = request.tgChatIds();
        String textMessage = "По ссылке %s появились обновления.\n%s".formatted(request.url(), request.description());
        for (Long chatId : chatIds) {
            SendMessage message = new SendMessage(chatId, textMessage);
            botController.sendMessage(message);
        }
        proceedMessagesCounter.increment();
    }
}
