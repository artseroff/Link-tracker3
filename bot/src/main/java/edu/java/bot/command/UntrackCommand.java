package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import edu.java.bot.exception.NotTrackedLinkException;
import edu.java.bot.exception.UserNotFoundException;
import edu.java.bot.service.UserService;
import edu.java.bot.service.link.AbstractLinkValidator;
import edu.java.bot.service.link.LinkUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand extends AbstractValidatedCommand {

    private static final String ONE_PARAMETER_MESSAGE =
        "Команда /untrack принимает один обязательный параметр - ссылку";

    private final UserService userService;
    private final AbstractLinkValidator headLinkValidator;

    @Autowired public UntrackCommand(
        UserService userService, @Qualifier("headLinkValidator") AbstractLinkValidator headLinkValidator
    ) {
        this.userService = userService;
        this.headLinkValidator = headLinkValidator;
    }

    @Override
    public String command() {
        return "untrack";
    }

    @Override
    public String description() {
        return "прекратить отслеживание ссылки [Синтаксис - /untrack <ссылка>]";
    }

    @Override
    public SendMessage execute(@NotNull ParameterizableTextCommand textCommand) {
        validate(textCommand);
        long chatId = textCommand.chatId();
        String message;
        try {
            userService.deleteLink(chatId, textCommand.rawParameter());
            message = "Ссылка %s удалена из списка отслеживаемых".formatted(textCommand.rawParameter());
        } catch (UserNotFoundException | NotTrackedLinkException e) {
            message = e.getMessage();
        }
        return new SendMessage(chatId, message);
    }

    @Override protected void validate(@NotNull ParameterizableTextCommand textCommand) {
        LinkUtils.checkLinkCorrectnessForTrackingOrThrow(textCommand, headLinkValidator, ONE_PARAMETER_MESSAGE);
    }
}
