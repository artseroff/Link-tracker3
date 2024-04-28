package edu.java.scrapper.configuration;

import edu.java.client.ClientConfigRecord;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.github.SimpleGithubClient;
import edu.java.scrapper.client.stackoverflow.SimpleStackoverflowClient;
import edu.java.scrapper.client.stackoverflow.StackoverflowClient;
import edu.java.scrapper.service.updater.sender.bot.BotClient;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientConfig(
    @NotNull ClientConfigRecord github,
    @NotNull ClientConfigRecord stackoverflow,
    @NotNull ClientConfigRecord bot
) {

    @Bean
    public GithubClient githubClient() {
        return new SimpleGithubClient(github);
    }

    @Bean
    public StackoverflowClient stackoverflowClient() {
        return new SimpleStackoverflowClient(stackoverflow);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "link-updater-type", havingValue = "http")
    public BotClient botClient() {
        return new BotClient(bot);
    }

}
