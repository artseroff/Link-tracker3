package edu.java.scrapper.domain.jooq.repository;

import edu.java.scrapper.domain.LinkRepository;
import edu.java.scrapper.domain.dto.LinkDto;
import edu.java.scrapper.domain.jooq.Tables;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Optional;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JooqLinkRepository implements LinkRepository {

    private final DSLContext dslContext;

    public JooqLinkRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Optional<LinkDto> findById(long id) {
        return dslContext
            .selectFrom(Tables.LINKS)
            .where(Tables.LINKS.ID.eq(id))
            .fetchOptional()
            .map(chatsRecord -> chatsRecord.into(LinkDto.class));
    }

    @Override
    public Optional<LinkDto> findByUrl(URI url) {
        return dslContext
            .selectFrom(Tables.LINKS)
            .where(Tables.LINKS.URL.eq(url.toString()))
            .fetchOptional()
            .map(chatsRecord -> chatsRecord.into(LinkDto.class));
    }

    @Override
    public LinkDto add(URI url, OffsetDateTime lastUpdatedAt, OffsetDateTime lastSchedulerCheck) {
        Optional<LinkDto> optionalLinkDto =
            dslContext
                .insertInto(Tables.LINKS)
                .set(Tables.LINKS.URL, url.toString())
                .set(Tables.LINKS.LAST_UPDATED_AT, lastUpdatedAt)
                .set(Tables.LINKS.LAST_SCHEDULER_CHECK, lastSchedulerCheck)
                .returning()
                .fetchOptional()
                .map(linksRecord -> linksRecord.into(LinkDto.class));

        return optionalLinkDto.get();

    }

    @Override
    public void remove(long id) {
        dslContext
            .deleteFrom(Tables.LINKS)
            .where(Tables.LINKS.ID.eq(id))
            .execute();
    }

    @Override
    public Collection<LinkDto> findAll() {
        return dslContext
            .selectFrom(Tables.LINKS)
            .fetchInto(LinkDto.class);
    }

    @Override
    public Collection<LinkDto> findAllBeforeLastSchedulerCheck(OffsetDateTime lastSchedulerCheck, long linksLimit) {
        return dslContext
            .selectFrom(Tables.LINKS)
            .where(Tables.LINKS.LAST_SCHEDULER_CHECK.isNull()
                .or(Tables.LINKS.LAST_SCHEDULER_CHECK.le(lastSchedulerCheck)))
            .orderBy(Tables.LINKS.LAST_SCHEDULER_CHECK.nullsFirst())
            .limit(linksLimit)
            .fetchInto(LinkDto.class);
    }

    @Override
    public void updateModifiedAndSchedulerCheckDates(long id, OffsetDateTime updatedAt, OffsetDateTime checkedAt) {
        dslContext
            .update(Tables.LINKS)
            .set(Tables.LINKS.LAST_UPDATED_AT, updatedAt)
            .set(Tables.LINKS.LAST_SCHEDULER_CHECK, checkedAt)
            .where(Tables.LINKS.ID.eq(id))
            .execute();
    }
}
