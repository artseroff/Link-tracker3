package edu.java.bot.configuration;

import edu.java.bot.client.scrapper.ScrapperClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScrapperClientConfig {
    @Value("${client.scrapper.baseUrl}")
    private String baseUrl;

    @Bean
    public ScrapperClient githubClient() {
        return new ScrapperClient(baseUrl);
    }
}
