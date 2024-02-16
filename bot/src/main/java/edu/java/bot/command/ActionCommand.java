package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import org.jetbrains.annotations.NotNull;

public interface ActionCommand {
    SendMessage execute(@NotNull ParameterizableTextCommand textCommand);
}
