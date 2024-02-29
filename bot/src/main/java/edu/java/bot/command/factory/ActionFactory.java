package edu.java.bot.command.factory;

import edu.java.bot.command.ActionCommand;
import edu.java.bot.command.UnknownCommand;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import java.util.Set;

public class ActionFactory {

    private final Set<ActionCommand> commands;

    public ActionFactory(Set<ActionCommand> commands) {
        this.commands = commands;
    }

    public ActionCommand defineCommand(ParameterizableTextCommand textCommand) {
        ActionCommand command = new UnknownCommand();
        String action = textCommand.command().toLowerCase();
        for (ActionCommand actionCommand : commands) {
            if (actionCommand.command().equals(action)) {
                command = actionCommand;
            }
        }
        return command;
    }
}
