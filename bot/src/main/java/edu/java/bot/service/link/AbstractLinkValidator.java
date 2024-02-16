package edu.java.bot.service.link;

import java.net.URL;

public abstract class AbstractLinkValidator {
    private AbstractLinkValidator next;

    public final void setNext(AbstractLinkValidator next) {
        if (this.equals(next)) {
            throw new IllegalArgumentException("В цепочке не могут состоять одинаковые объекты");
        }
        this.next = next;
    }

    abstract String getHostName();

    public final boolean isValid(URL url) {
        if (getHostName().equals(url.getHost())) {
            return true;
        }
        if (next != null) {
            return next.isValid(url);
        }
        return false;
    }
}

