package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import edu.java.bot.exception.AlreadyTrackedLinkException;
import edu.java.bot.exception.UserNotFoundException;
import edu.java.bot.service.UserService;
import edu.java.bot.service.link.AbstractLinkValidator;
import edu.java.bot.service.link.LinkUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component public class TrackCommand extends AbstractValidatedCommand {

    private static final String ONE_PARAMETER_MESSAGE = "Команда /track принимает один обязательный параметр - ссылку";
    private final UserService userService;
    private final AbstractLinkValidator headLinkValidator;

    @Autowired public TrackCommand(
        UserService userService, @Qualifier("headLinkValidator") AbstractLinkValidator headLinkValidator
    ) {
        this.userService = userService;
        this.headLinkValidator = headLinkValidator;
    }

    @Override public String command() {
        return "track";
    }

    @Override public String description() {
        return "начать отслеживание ссылки [Синтаксис - /track <ссылка>]";
    }

    @Override public SendMessage execute(@NotNull ParameterizableTextCommand textCommand) {
        validate(textCommand);
        long chatId = textCommand.chatId();
        String message;
        try {
            userService.addLink(chatId, textCommand.rawParameter());
            message = "Ссылка %s добавлена в список отслеживаемых".formatted(textCommand.rawParameter());
        } catch (UserNotFoundException | AlreadyTrackedLinkException e) {
            message = e.getMessage();
        }
        return new SendMessage(chatId, message);
    }

    @Override protected void validate(@NotNull ParameterizableTextCommand textCommand) {
        LinkUtils.checkLinkCorrectnessForTrackingOrThrow(textCommand, headLinkValidator, ONE_PARAMETER_MESSAGE);
    }
}
