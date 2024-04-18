package edu.java.bot.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeterConfig {
    @Bean
    public Counter proceedMessagesCounter(MeterRegistry meterRegistry) {
        return Counter.builder("proceed.messages")
            .register(meterRegistry);
    }

    @Bean
    public Counter errorsCounter(MeterRegistry meterRegistry) {
        return Counter.builder("error.messages")
            .register(meterRegistry);
    }
}
