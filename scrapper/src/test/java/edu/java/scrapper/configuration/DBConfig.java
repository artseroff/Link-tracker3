package edu.java.scrapper.configuration;

import edu.java.scrapper.integration.IntegrationTest;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
public class DBConfig {
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
            .url(IntegrationTest.POSTGRES.getJdbcUrl())
            .username(IntegrationTest.POSTGRES.getUsername())
            .password(IntegrationTest.POSTGRES.getPassword())
            .build();
    }

    @Bean
    public JdbcClient jdbcClient() {
        return JdbcClient.create(dataSource());
    }

}
