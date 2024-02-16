package edu.java.bot.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.SendResponse;
import edu.java.bot.command.ActionCommand;
import edu.java.bot.command.client.CommandEnum;
import edu.java.bot.command.factory.ActionFactory;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import edu.java.bot.configuration.ApplicationConfig;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BotController implements AutoCloseable {
    private final ApplicationConfig config;
    private TelegramBot bot;

    @Autowired
    public BotController(ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public void close() throws Exception {
        bot.removeGetUpdatesListener();
        //bot.shutdown();
    }

    @EventListener(ContextRefreshedEvent.class)
    private void init() {
        bot = new TelegramBot(config.telegramToken());
        setUpdatesListener();
        BotCommand[] botCommands = Arrays.stream(CommandEnum.values())
            .map(commandEnum -> new BotCommand(commandEnum.getCommandKey(), commandEnum.getDescription()))
            .toArray(BotCommand[]::new);
        bot.execute(new SetMyCommands(botCommands));
    }

    private void setUpdatesListener() {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                try {
                    ParameterizableTextCommand textCommand =
                        ParameterizableTextCommand.buildTextCommandFromUpdate(update);
                    ActionCommand command = ActionFactory.defineCommand(textCommand);
                    sendMessage(command.execute(textCommand));
                } catch (IllegalArgumentException e) {
                    long chatId = update.message().chat().id();
                    SendMessage request = new SendMessage(chatId, e.getMessage());
                    sendMessage(request);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> log.error(e.getMessage()));
    }

    private void sendMessage(SendMessage message) {
        SendResponse response = bot.execute(message);
        if (!response.isOk()) {
            log.error("%s;%s".formatted(response.errorCode(), response.description()));
        }
    }
}
