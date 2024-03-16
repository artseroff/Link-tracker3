package edu.java.scrapper.api.service.updater;

import edu.java.scrapper.api.service.exception.NotSupportedLinkException;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class FetchersChainUtils {
    public static final String SECURE_HYPER_TEXT_PROTOCOL = "https://";
    public static final String HYPER_TEXT_PROTOCOL = "http://";

    private FetchersChainUtils() {
    }

    public static void throwIfProtocolAbsent(@NotNull String text) throws NotSupportedLinkException {
        String lowerCaseText = text.toLowerCase();
        if (!lowerCaseText.startsWith(SECURE_HYPER_TEXT_PROTOCOL) && !lowerCaseText.startsWith(HYPER_TEXT_PROTOCOL)) {
            throw new NotSupportedLinkException("Протокол ссылки %s не указан".formatted(text));
        }
    }

    public static AbstractUpdatesFetcher buildChain(@NotNull Set<AbstractUpdatesFetcher> fetchers) {
        AbstractUpdatesFetcher current = null;
        AbstractUpdatesFetcher first = null;
        for (AbstractUpdatesFetcher nextValidator : fetchers) {
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

}
