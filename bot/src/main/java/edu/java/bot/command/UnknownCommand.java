package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import org.jetbrains.annotations.NotNull;

public class UnknownCommand implements ActionCommand {
    public static final String MESSAGE = "Введена неизвестная команда";

    @Override
    public SendMessage execute(@NotNull ParameterizableTextCommand textCommand) {
        long chatId = textCommand.chatId();
        return new SendMessage(chatId, MESSAGE);
    }

}
