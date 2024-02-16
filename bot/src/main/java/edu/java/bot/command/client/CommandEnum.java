package edu.java.bot.command.client;

import edu.java.bot.command.ActionCommand;
import edu.java.bot.command.HelpCommand;
import edu.java.bot.command.ListCommand;
import edu.java.bot.command.StartCommand;
import edu.java.bot.command.TrackCommand;
import edu.java.bot.command.UntrackCommand;
import lombok.Getter;

@Getter
public enum CommandEnum {
    START(new StartCommand(), "зарегистрировать пользователя"),
    HELP(new HelpCommand(), "вывести окно с командами"),
    TRACK(new TrackCommand(), "начать отслеживание ссылки"),
    UNTRACK(new UntrackCommand(), "прекратить отслеживание ссылки"),
    LIST(new ListCommand(), "показать список отслеживаемых ссылок");

    public static final char FIRST_COMMAND_SYMBOL = '/';

    private final ActionCommand command;

    private final String description;

    CommandEnum(ActionCommand command, String description) {
        this.command = command;
        this.description = description;
    }

    public String getCommandKey() {
        return FIRST_COMMAND_SYMBOL + name().toLowerCase();
    }
}
