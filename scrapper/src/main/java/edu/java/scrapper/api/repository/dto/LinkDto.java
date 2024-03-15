package edu.java.scrapper.api.repository.dto;

import java.net.URI;
import java.time.OffsetDateTime;

public record LinkDto(long id, URI url, OffsetDateTime lastUpdatedAt, OffsetDateTime lastSchedulerCheck) {
}
