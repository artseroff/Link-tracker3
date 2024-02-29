package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import edu.java.bot.exception.UserNotFoundException;
import edu.java.bot.service.UserService;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListCommand extends AbstractValidatedCommand {
    private static final String NUMBERED_LIST_FORMAT = "%d) %s\n";
    private final UserService userService;

    @Autowired
    public ListCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public String description() {
        return "показать список отслеживаемых ссылок";
    }

    @Override
    public SendMessage execute(@NotNull ParameterizableTextCommand textCommand) {
        validate(textCommand);
        long chatId = textCommand.chatId();

        String message;
        try {
            List<String> links = userService.listTrackedLinkByUserId(chatId);
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
        StringBuilder result = new StringBuilder("Вы отслеживаете следующие ссылки:\n");
        for (int i = 0; i < links.size(); i++) {
            result.append(NUMBERED_LIST_FORMAT.formatted(i + 1, links.get(i)));
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    @Override
    protected void validate(@NotNull ParameterizableTextCommand textCommand) {
        String rawParameter = textCommand.rawParameter();
        if (rawParameter != null) {
            throw new IllegalArgumentException("Команда /list не имеет параметров");
        }
    }
}
