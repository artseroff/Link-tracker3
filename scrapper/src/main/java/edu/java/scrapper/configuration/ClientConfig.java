package edu.java.scrapper.configuration;

import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.github.SimpleGithubClient;
import edu.java.scrapper.client.stackoverflow.SimpleStackoverflowClient;
import edu.java.scrapper.client.stackoverflow.StackoverflowClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    @Value("${client.github.baseUrl}")
    private String baseGithubUrl;

    @Value("${client.stackoverflow.baseUrl}")
    private String baseStackOverFlowUrl;

    @Bean
    public GithubClient githubClient() {
        return new SimpleGithubClient(baseGithubUrl);
    }

    @Bean
    public StackoverflowClient stackoverflowClient() {
        return new SimpleStackoverflowClient(baseStackOverFlowUrl);
    }
}
