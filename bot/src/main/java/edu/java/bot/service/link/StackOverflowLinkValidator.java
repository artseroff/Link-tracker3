package edu.java.bot.service.link;

import org.springframework.stereotype.Component;

@Component
public class StackOverflowLinkValidator extends AbstractLinkValidator {
    @Override public String getHostName() {
        return "stackoverflow.com";
    }
}
