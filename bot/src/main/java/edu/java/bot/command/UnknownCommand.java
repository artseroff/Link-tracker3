package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import org.jetbrains.annotations.NotNull;

public class UnknownCommand implements ActionCommand {
    public static final String UNKNOWN_COMMAND_MESSAGE = "Введена неизвестная команда";

    public static final String EMPTY_OBJECT_STRING = "null";

    @Override
    public String command() {
        return EMPTY_OBJECT_STRING;
    }

    @Override
    public String description() {
        return EMPTY_OBJECT_STRING;
    }

    @Override
    public SendMessage execute(@NotNull ParameterizableTextCommand textCommand) {
        long chatId = textCommand.chatId();
        return new SendMessage(chatId, UNKNOWN_COMMAND_MESSAGE);
    }

}
