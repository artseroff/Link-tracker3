package edu.java.bot.configuration;

import edu.java.bot.client.scrapper.ScrapperClient;
import edu.java.client.ClientConfigRecord;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ScrapperClientConfig(
    @NotNull ClientConfigRecord scrapper
) {

    @Bean
    public ScrapperClient githubClient() {
        return new ScrapperClient(scrapper);
    }
}
