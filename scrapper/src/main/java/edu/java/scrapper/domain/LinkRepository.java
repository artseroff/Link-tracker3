package edu.java.scrapper.domain;

import edu.java.scrapper.domain.dto.LinkDto;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Optional;

public interface LinkRepository {
    Optional<LinkDto> findById(long id);

    Optional<LinkDto> findByUrl(URI url);

    LinkDto add(URI url, OffsetDateTime lastUpdatedAt, OffsetDateTime lastSchedulerCheck);

    void remove(long id);

    Collection<LinkDto> findAll();

    Collection<LinkDto> findAllBeforeLastSchedulerCheck(OffsetDateTime lastSchedulerCheck, long linksLimit);

    void updateModifiedAndSchedulerCheckDates(long id, OffsetDateTime updatedAt, OffsetDateTime checkedAt);
}
