package edu.java.scrapper.domain.dto;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LinkDto linkDto = (LinkDto) o;
        return id == linkDto.id && Objects.equals(url, linkDto.url)
            && TimeDifferenceUtils.isTimeEqualWithEpsilon(lastUpdatedAt, linkDto.lastUpdatedAt)
            && TimeDifferenceUtils.isTimeEqualWithEpsilon(lastSchedulerCheck, linkDto.lastSchedulerCheck);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, lastUpdatedAt, lastSchedulerCheck);
    }
}
