package edu.java.bot.configuration;

import edu.java.bot.command.ActionCommand;
import edu.java.bot.command.HelpCommand;
import java.util.Set;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandsConfiguration {

    public CommandsConfiguration(HelpCommand helpCommand, Set<ActionCommand> commands) {
        helpCommand.setCommands(commands);
    }

}
