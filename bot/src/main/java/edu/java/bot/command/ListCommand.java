package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.ScrapperClient;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import edu.java.response.LinkResponse;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListCommand extends AbstractValidatedCommand {
    private static final String NUMBERED_LIST_FORMAT = "%d) %s\n";

    private final ScrapperClient scrapperClient;

    @Autowired
    public ListCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public String command() {
        return "list";
    }

    @Override
    public String description() {
        return "показать список отслеживаемых ссылок";
    }

    @Override
    public SendMessage execute(@NotNull ParameterizableTextCommand textCommand) {
        validate(textCommand);
        long chatId = textCommand.chatId();

        String message;

        List<LinkResponse> links = scrapperClient.getLinks(chatId).list();

        message = buildTextListFromLinks(links);

        return new SendMessage(chatId, message);
    }

    private String buildTextListFromLinks(List<LinkResponse> links) {
        if (links.isEmpty()) {
            return "Список отслеживаемых ссылок пуст";
        }
        StringBuilder result = new StringBuilder("Вы отслеживаете следующие ссылки:\n");
        for (int i = 0; i < links.size(); i++) {
            result.append(NUMBERED_LIST_FORMAT.formatted(i + 1, links.get(i).url()));
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    @Override
    protected void validate(@NotNull ParameterizableTextCommand textCommand) {
        String rawParameter = textCommand.rawParameter();
        if (rawParameter != null) {
            throw new IllegalArgumentException("Команда /list не имеет параметров");
        }
    }
}
