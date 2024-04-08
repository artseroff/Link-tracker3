package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.ScrapperClient;
import edu.java.bot.command.raw.ParameterizableTextCommand;
import edu.java.bot.service.link.AbstractLinkValidator;
import edu.java.bot.service.link.LinkUtils;
import edu.java.general.LinkSubscriptionDto;
import edu.java.response.LinkResponse;
import java.net.URI;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand extends AbstractValidatedCommand {

    private static final String ONE_PARAMETER_MESSAGE =
        "Команда /untrack принимает один обязательный параметр - ссылку";

    private final ScrapperClient scrapperClient;
    private final AbstractLinkValidator headLinkValidator;

    @Autowired
    public UntrackCommand(
        ScrapperClient scrapperClient,
        @Qualifier("headLinkValidator") AbstractLinkValidator headLinkValidator
    ) {
        this.scrapperClient = scrapperClient;
        this.headLinkValidator = headLinkValidator;
    }

    @Override
    public String command() {
        return "untrack";
    }

    @Override
    public String description() {
        return "прекратить отслеживание ссылки [Синтаксис - /untrack <ссылка>]";
    }

    @Override
    public SendMessage execute(@NotNull ParameterizableTextCommand textCommand) {
        validate(textCommand);
        long chatId = textCommand.chatId();
        LinkSubscriptionDto linkSubscriptionDto =
            new LinkSubscriptionDto(chatId, URI.create(textCommand.rawParameter()));
        LinkResponse linkResponse = scrapperClient.deleteLink(linkSubscriptionDto);

        String message = "Ссылка %s удалена из списка отслеживаемых".formatted(linkResponse.url());
        return new SendMessage(chatId, message);
    }

    @Override
    protected void validate(@NotNull ParameterizableTextCommand textCommand) {
        LinkUtils.checkLinkCorrectnessForTrackingOrThrow(textCommand, headLinkValidator, ONE_PARAMETER_MESSAGE);
    }
}
