package edu.java.scrapper.configuration;

import edu.java.scrapper.api.service.updater.AbstractUpdatesFetcher;
import edu.java.scrapper.api.service.updater.FetchersChainUtils;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdatesFetchersChainConfig {

    private final Set<AbstractUpdatesFetcher> fetcherSet;

    public UpdatesFetchersChainConfig(Set<AbstractUpdatesFetcher> fetcherSet) {
        this.fetcherSet = fetcherSet;
    }

    @Bean
    public AbstractUpdatesFetcher headUpdatesFetcher() {
        return FetchersChainUtils.buildChain(fetcherSet);
    }
}
