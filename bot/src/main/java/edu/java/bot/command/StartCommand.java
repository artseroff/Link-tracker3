package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import edu.java.bot.service.UserService;
import org.jetbrains.annotations.NotNull;

public class StartCommand extends AbstractValidatedCommand {

    @Override
    public SendMessage execute(@NotNull ParameterizableTextCommand textCommand) {
        validate(textCommand);
        long chatId = textCommand.chatId();
        String message = "Вы уже зарегистрированы";

        if (UserService.getInstance().addUser(chatId)) {
            message = "Вы успешно зарегистрировались";
        }
        return new SendMessage(chatId, message);
    }

    @Override
    protected void validate(@NotNull ParameterizableTextCommand textCommand) {
        String rawParameter = textCommand.rawParameter();
        if (rawParameter != null) {
            throw new IllegalArgumentException("Команда /start не имеет параметров");
        }
    }
}
