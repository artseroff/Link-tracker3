package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import edu.java.bot.exception.NotTrackedLinkException;
import edu.java.bot.exception.UserNotFoundException;
import edu.java.bot.service.UserService;
import org.jetbrains.annotations.NotNull;

public class UntrackCommand extends AbstractValidatedCommand {

    private static final String ONE_PARAMETER_MESSAGE =
        "Команда /untrack принимает один обязательный параметр - ссылку";

    @Override
    public SendMessage execute(@NotNull ParameterizableTextCommand textCommand) {
        validate(textCommand);
        long chatId = textCommand.chatId();
        String message;
        try {
            UserService.getInstance().deleteLink(chatId, textCommand.rawParameter());
            message = "Ссылка %s удалена из списка отслеживаемых".formatted(textCommand.rawParameter());
        } catch (UserNotFoundException | NotTrackedLinkException e) {
            message = e.getMessage();
        }
        return new SendMessage(chatId, message);
    }

    @Override
    protected void validate(@NotNull ParameterizableTextCommand textCommand) {
        String rawParameter = textCommand.rawParameter();
        if (rawParameter == null) {
            throw new IllegalArgumentException(ONE_PARAMETER_MESSAGE);
        }

        String[] tokens = rawParameter.split("\\s+");
        if (tokens.length != 1) {
            throw new IllegalArgumentException(ONE_PARAMETER_MESSAGE);
        }

    }
}
