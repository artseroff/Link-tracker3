package edu.java.scrapper.service.updater;

import java.net.URI;
import java.time.OffsetDateTime;

public record LinkUpdateDescription(URI url, OffsetDateTime lastUpdatedAt, OffsetDateTime lastSchedulerCheck,
                                    String description) {
}
