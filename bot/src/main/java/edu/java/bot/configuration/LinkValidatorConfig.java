package edu.java.bot.configuration;

import edu.java.bot.service.link.AbstractLinkValidator;
import edu.java.bot.service.link.LinkUtils;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LinkValidatorConfig {

    private final Set<AbstractLinkValidator> linkValidatorSet;

    public LinkValidatorConfig(Set<AbstractLinkValidator> linkValidatorSet) {
        this.linkValidatorSet = linkValidatorSet;
    }

    @Bean
    AbstractLinkValidator headLinkValidator() {
        return LinkUtils.buildChain(linkValidatorSet);
    }
}
