package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.SendResponse;
import edu.java.bot.command.ActionCommand;
import edu.java.bot.command.factory.ActionFactory;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BotController implements AutoCloseable {
    private static final String BOT_PROCESS_ONLY_PLAIN_TEXT = "Введите новое сообщение. "
        + "Бот не умеет считывать изменения прошлых собщений и может "
        + "принимать только сообщения из обычного текста без стикеров и ссылок на другие чаты.";

    private final Set<ActionCommand> commands;
    private final ActionFactory actionFactory;
    private final TelegramBot bot;

    @Autowired
    public BotController(TelegramBot bot, Set<ActionCommand> commands) {
        this.bot = bot;
        this.commands = commands;
        this.actionFactory = new ActionFactory(commands);
    }

    @Override
    public void close() {
        bot.removeGetUpdatesListener();
        bot.shutdown();
    }

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        setUpdatesListener();
        setBotMenu();
    }

    private void setBotMenu() {
        BotCommand[] botCommands = commands.stream()
            .map(command -> new BotCommand(command.command(), command.description()))
            .toArray(BotCommand[]::new);
        bot.execute(new SetMyCommands(botCommands));
    }

    private void setUpdatesListener() {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {

                SendMessage message = handleUpdate(update);

                sendMessage(message);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> log.error(e.getMessage()));
    }

    public SendMessage handleUpdate(Update update) {
        if (update.message() == null) {
            long chatId = getChatId(update);
            return new SendMessage(
                chatId,
                BOT_PROCESS_ONLY_PLAIN_TEXT
            );
        }
        SendMessage message;
        try {
            ParameterizableTextCommand textCommand =
                ParameterizableTextCommand.buildTextCommandFromUpdate(update);
            ActionCommand command = actionFactory.defineCommand(textCommand);
            message = command.execute(textCommand);
        } catch (RuntimeException e) {
            long chatId = update.message().chat().id();
            message = new SendMessage(chatId, e.getMessage());
        }
        return message;
    }

    private long getChatId(Update update) {
        long chatId = 0;
        if (update.editedMessage() != null) {
            chatId = update.editedMessage().chat().id();
        }
        if (update.inlineQuery() != null) {
            chatId = update.inlineQuery().from().id();
        }
        if (update.chosenInlineResult() != null) {
            chatId = update.chosenInlineResult().from().id();
        }
        if (update.callbackQuery() != null) {
            chatId = update.callbackQuery().from().id();
        }
        return chatId;
    }

    public void sendMessage(SendMessage message) {
        SendResponse response = bot.execute(message);
        if (!response.isOk()) {
            log.error("%s;%s".formatted(response.errorCode(), response.description()));
        }
    }
}
