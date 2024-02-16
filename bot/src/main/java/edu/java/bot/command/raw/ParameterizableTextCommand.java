package edu.java.bot.command.raw;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.command.client.CommandEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ParameterizableTextCommand(long chatId, @NotNull String command,
                                         @Nullable String rawParameter) {

    public static ParameterizableTextCommand buildTextCommandFromUpdate(@NotNull Update update) {

        long chatId = update.message().chat().id();
        String messageText = update.message().text().strip();
        if (messageText.isEmpty()) {
            throw new IllegalArgumentException("Передана пустая команда");
        }
        if (messageText.charAt(0) != CommandEnum.FIRST_COMMAND_SYMBOL) {
            throw new IllegalArgumentException("Команда должна начинаться с '%c'"
                .formatted(CommandEnum.FIRST_COMMAND_SYMBOL));
        }
        String[] tokens = messageText.split("\\s+", 2);
        String command = tokens[0].substring(1);
        String rawParameter = null;
        if (tokens.length > 1) {
            rawParameter = tokens[1].stripTrailing();
        }
        return new ParameterizableTextCommand(chatId, command, rawParameter);
    }
}
