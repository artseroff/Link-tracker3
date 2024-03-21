package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.command.ActionCommand;
import edu.java.bot.command.HelpCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Set;

@Configuration
public class TelegramBotConfiguration {

    public TelegramBotConfiguration(HelpCommand helpCommand, Set<ActionCommand> commands) {
        helpCommand.SetCommands(commands);
    }

    @Bean
    public TelegramBot telegramBot(ApplicationConfig config) {
        return new TelegramBot(config.telegramToken());
    }


}
