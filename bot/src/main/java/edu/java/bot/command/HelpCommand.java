package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import edu.java.bot.service.link.AbstractLinkValidator;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand extends AbstractValidatedCommand {
    private static final String COMMAND_REPRESENTATION_FORMAT =
        ActionCommand.FIRST_COMMAND_SYMBOL + "%s -- %s;";

    private static final String CURRENT_AVAILABLE_LINKS_TEXT =
        "\nНа данный момент для отслеживания доступны только следующие сервисы:\n";

    private final Set<AbstractLinkValidator> linkValidatorSet;

    @Setter
    private Set<ActionCommand> commands;

    @Autowired
    public HelpCommand(Set<AbstractLinkValidator> linkValidatorSet) {
        this.linkValidatorSet = linkValidatorSet;
    }

    @Override
    public String command() {
        return "help";
    }

    @Override
    public String description() {
        return "вывести окно с командами";
    }

    @Override
    public SendMessage execute(@NotNull ParameterizableTextCommand textCommand) {
        validate(textCommand);
        return new SendMessage(textCommand.chatId(), buildHelpText());
    }

    @Override
    protected void validate(@NotNull ParameterizableTextCommand textCommand) {
        String rawParameter = textCommand.rawParameter();
        if (rawParameter != null) {
            throw new IllegalArgumentException("Команда /help не имеет параметров");
        }
    }

    private String buildHelpText() {
        return commands.stream()
            .map(command -> COMMAND_REPRESENTATION_FORMAT.formatted(
                command.command().toLowerCase(),
                command.description()
            )).collect(Collectors.joining("\n"))
            + CURRENT_AVAILABLE_LINKS_TEXT
            + linkValidatorSet.stream()
            .map(AbstractLinkValidator::getHostName)
            .collect(Collectors.joining("; "));
    }

}
