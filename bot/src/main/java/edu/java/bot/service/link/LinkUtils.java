package edu.java.bot.service.link;

import edu.java.bot.command.raw.ParameterizableTextCommand;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class LinkUtils {
    public static final String SECURE_HYPER_TEXT_PROTOCOL = "https://";
    public static final String HYPER_TEXT_PROTOCOL = "http://";

    private LinkUtils() {
    }

    public static URL textToUrl(@NotNull String text) {
        String lowerCaseText = text.toLowerCase();
        if (!lowerCaseText.startsWith(SECURE_HYPER_TEXT_PROTOCOL) && !lowerCaseText.startsWith(HYPER_TEXT_PROTOCOL)) {
            throw new IllegalArgumentException("Протокол ссылки не указан");
        }
        try {
            return new URI(text).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException("Введена некорректная ссылка");
        }
    }

    public static AbstractLinkValidator buildChain(@NotNull Set<AbstractLinkValidator> validators) {
        AbstractLinkValidator current = null;
        AbstractLinkValidator first = null;
        for (AbstractLinkValidator nextValidator : validators) {
            if (first == null) {
                first = nextValidator;
                current = nextValidator;
                continue;
            }
            current.setNext(nextValidator);
            current = nextValidator;
        }
        return first;
    }

    public static void checkLinkCorrectnessForTrackingOrThrow(
        @NotNull ParameterizableTextCommand textCommand,
        @NotNull AbstractLinkValidator linkValidator,
        @NotNull String oneParameterMessage
    ) {
        String rawParameter = textCommand.rawParameter();
        if (rawParameter == null) {
            throw new IllegalArgumentException(oneParameterMessage);
        }

        String[] tokens = rawParameter.split("\\s+");
        if (tokens.length != 1) {
            throw new IllegalArgumentException(oneParameterMessage);
        }

        URL url = LinkUtils.textToUrl(textCommand.rawParameter());
        if (!linkValidator.isValid(url)) {
            throw new IllegalArgumentException("Введена ссылка, которая не поддерживается для отслеживания");
        }
    }

}
