package edu.java.scrapper.service.updater;

import edu.java.scrapper.service.exception.NotSupportedLinkException;
import java.net.URI;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class FetchersChainUtils {
    public static final String URL_DELIMITER = "/";
    public static final String SECURE_HYPER_TEXT_PROTOCOL = "https://";
    public static final String HYPER_TEXT_PROTOCOL = "http://";
    private static final String END_OF_LONG_DESCRIPTION = "...";

    private FetchersChainUtils() {
    }

    public static void throwIfProtocolAbsent(@NotNull String text) throws NotSupportedLinkException {
        String lowerCaseText = text.toLowerCase();
        if (!lowerCaseText.startsWith(SECURE_HYPER_TEXT_PROTOCOL) && !lowerCaseText.startsWith(HYPER_TEXT_PROTOCOL)) {
            throw new NotSupportedLinkException("Протокол ссылки %s не указан".formatted(text));
        }
    }

    public static URI createUrl(String protocol, String host, String urlPath) {
        return URI.create(protocol + host + URL_DELIMITER + urlPath);
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

    public static String makeStringLessThanBound(String description, int bound) {
        if (description.length() <= bound) {
            return description;
        }
        return description.substring(0, bound - END_OF_LONG_DESCRIPTION.length()) + END_OF_LONG_DESCRIPTION;
    }

}
