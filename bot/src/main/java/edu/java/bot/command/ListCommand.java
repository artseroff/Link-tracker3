package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import edu.java.bot.exception.UserNotFoundException;
import edu.java.bot.service.UserService;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class ListCommand extends AbstractValidatedCommand {
    private static final String NUMBERED_LIST_FORMAT = "%d) %s\n";

    @Override
    public SendMessage execute(@NotNull ParameterizableTextCommand textCommand) {
        validate(textCommand);
        long chatId = textCommand.chatId();

        String message;
        try {
            List<String> links = UserService.getInstance().listTrackedLinkByUserId(chatId);
            if (links.isEmpty()) {
                message = "Список отслеживаемых ссылок пуст";
            } else {
                message = buildTextListFromLinks(links);
            }
        } catch (UserNotFoundException e) {
            message = e.getMessage();
        }

        return new SendMessage(chatId, message);
    }

    private String buildTextListFromLinks(List<String> links) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < links.size(); i++) {
            result.append(NUMBERED_LIST_FORMAT.formatted(i + 1, links.get(i)));
        }
        return result.toString();
    }

    @Override
    protected void validate(@NotNull ParameterizableTextCommand textCommand) {
        String rawParameter = textCommand.rawParameter();
        if (rawParameter != null) {
            throw new IllegalArgumentException("Команда /start не имеет параметров");
        }
    }
}
