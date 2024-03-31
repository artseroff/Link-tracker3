package edu.java.scrapper.configuration;

import edu.java.limiter.RateLimiterFilter;
import edu.java.limiter.RateLimiterService;
import io.github.bucket4j.Bandwidth;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "rate-limiter", ignoreUnknownFields = false)
@ConditionalOnProperty(prefix = "rate-limiter", name = "enable")
public record RateLimiterConfig(
    boolean enable,
    long capacity,
    @NotNull RefillRecord refill
) {

    @Bean
    public Bandwidth bandwidth() {
        return Bandwidth.builder()
            .capacity(capacity)
            .refillIntervally(refill.tokens(), refill.period())
            .build();
    }

    @Bean
    public RateLimiterService rateLimiterService(Bandwidth bandwidth) {
        return new RateLimiterService(bandwidth);
    }

    @Bean
    public FilterRegistrationBean<RateLimiterFilter> rateLimiterFilterRegistrationBean(
        RateLimiterService rateLimiterService
    ) {
        FilterRegistrationBean<RateLimiterFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(new RateLimiterFilter(rateLimiterService));
        registrationBean.setOrder(1);

        return registrationBean;
    }

    public record RefillRecord(long tokens, @NotNull Duration period) {
    }
}
