package edu.java.bot.command;

import edu.java.bot.command.raw.ParameterizableTextCommand;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractValidatedCommand implements ActionCommand {
    protected abstract void validate(@NotNull ParameterizableTextCommand textCommand);
}
