package edu.java.scrapper.configuration;

import edu.java.scrapper.client.bot.BotClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotClientConfig {
    @Value("${client.bot.baseUrl}")
    private String baseGithubUrl;

    @Bean
    public BotClient githubClient() {
        return new BotClient(baseGithubUrl);
    }
}
