package edu.java.bot.service.link;

import org.springframework.stereotype.Component;

@Component
public class GitHubLinkValidator extends AbstractLinkValidator {
    @Override public String getHostName() {
        return "github.com";
    }
}
