package edu.java.scrapper.domain.dto;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public record LinkDto(long id, URI url, OffsetDateTime lastUpdatedAt, OffsetDateTime lastSchedulerCheck) {
    public LinkDto {
        lastUpdatedAt = truncateToUtc(lastUpdatedAt);
        lastSchedulerCheck = truncateToUtc(lastSchedulerCheck);
    }

    private static OffsetDateTime truncateToUtc(OffsetDateTime time) {
        if (time == null) {
            return null;
        }
        return time.withOffsetSameInstant(ZoneOffset.UTC)
            .truncatedTo(ChronoUnit.SECONDS);

    }
}
