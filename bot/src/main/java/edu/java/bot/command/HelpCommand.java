package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.client.CommandEnum;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class HelpCommand extends AbstractValidatedCommand {

    private static final String COMMAND_REPRESENTATION_FORMAT =
        CommandEnum.FIRST_COMMAND_SYMBOL + "%s -- %s";

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
        return Arrays.stream(CommandsLazyInitializer.COMMANDS)
            .map(commandEnum -> COMMAND_REPRESENTATION_FORMAT.formatted(
                commandEnum.name().toLowerCase(),
                commandEnum.getDescription()
            )).collect(Collectors.joining("\n"));
    }

    private static class CommandsLazyInitializer {
        private static final CommandEnum[] COMMANDS = CommandEnum.values();
    }

}
