package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.ScrapperClient;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartCommand extends AbstractValidatedCommand {

    private final ScrapperClient scrapperClient;

    @Autowired
    public StartCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public String command() {
        return "start";
    }

    @Override
    public String description() {
        return "зарегистрировать пользователя";
    }

    @Override
    public SendMessage execute(@NotNull ParameterizableTextCommand textCommand) {
        validate(textCommand);
        long chatId = textCommand.chatId();
        scrapperClient.createChat(chatId);
        String message = "Вы успешно зарегистрировались";
        return new SendMessage(chatId, message);
    }

    @Override
    protected void validate(@NotNull ParameterizableTextCommand textCommand) {
        String rawParameter = textCommand.rawParameter();
        if (rawParameter != null) {
            throw new IllegalArgumentException("Команда /start не имеет параметров");
        }
    }
}
