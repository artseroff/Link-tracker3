package edu.java.bot.command.factory;

import edu.java.bot.command.ActionCommand;
import edu.java.bot.command.UnknownCommand;
import edu.java.bot.command.client.CommandEnum;
import edu.java.bot.command.raw.ParameterizableTextCommand;

public class ActionFactory {
    private ActionFactory() {
    }

    public static ActionCommand defineCommand(ParameterizableTextCommand textCommand) {
        ActionCommand command = new UnknownCommand();
        String action = textCommand.command().toUpperCase();
        CommandEnum[] enumCommands = CommandEnum.values();
        for (CommandEnum enumCommandElement : enumCommands) {
            if (enumCommandElement.name().equals(action)) {
                command = enumCommandElement.getCommand();
            }
        }
        return command;
    }
}
