package edu.java.scrapper.domain.jooq.repository;

import edu.java.scrapper.domain.LinkRepository;
import edu.java.scrapper.domain.dto.ChatDto;
import edu.java.scrapper.domain.dto.LinkDto;
import edu.java.scrapper.domain.jooq.Tables;
import edu.java.scrapper.domain.jooq.tables.Chats;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
@Transactional
public class JooqLinkRepository implements LinkRepository {
    private final JdbcClient jdbcClient;
    private final DSLContext dslContext;

    public JooqLinkRepository(JdbcClient jdbcClient, DSLContext dslContext) {
        this.jdbcClient = jdbcClient;
        this.dslContext = dslContext;
    }

    @Override
    public Optional<LinkDto> findById(long id) {
        return dslContext
            .selectFrom(Tables.LINKS)
            .where(Tables.LINKS.ID.eq((int) id))
            .fetchOptional()
            .map(chatsRecord -> chatsRecord.into(LinkDto.class));
    }

    @Override
    public Optional<LinkDto> findByUrl(URI url) {
        return jdbcClient.sql("SELECT * FROM links WHERE url = ?")
            .param(url.toString())
            .query(LinkDto.class)
            .optional();
    }

    @Override
    public LinkDto add(URI url, OffsetDateTime lastUpdatedAt, OffsetDateTime lastSchedulerCheck) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("INSERT INTO links (url,last_updated_at,last_scheduler_check) VALUES (?,?,?)")
            .param(url.toString())
            .param(lastUpdatedAt)
            .param(lastSchedulerCheck)
            .update(keyHolder, "id");
        long id = keyHolder.getKey().longValue();
        return new LinkDto(id, url, lastUpdatedAt, lastSchedulerCheck);
    }

    @Override
    public void remove(long id) {
        jdbcClient.sql("DELETE FROM links WHERE id = ?")
            .param(id)
            .update();
    }

    @Override
    public Collection<LinkDto> findAll() {
        return jdbcClient.sql("SELECT * FROM links")
            .query(LinkDto.class)
            .list();
    }

    @Override
    public Collection<LinkDto> findAllBeforeLastSchedulerCheck(OffsetDateTime lastSchedulerCheck, long linksLimit) {
        String sql = """
            SELECT *
                FROM links
                WHERE last_scheduler_check IS NULL OR last_scheduler_check <= ?
                ORDER BY last_scheduler_check NULLS FIRST
                LIMIT ?
            """;
        return jdbcClient.sql(sql)
            .param(lastSchedulerCheck)
            .param(linksLimit)
            .query(LinkDto.class)
            .list();
    }

    @Override
    public void updateModifiedAndSchedulerCheckDates(long id, OffsetDateTime updatedAt, OffsetDateTime checkedAt) {
        String sql = """
            UPDATE links
                SET last_updated_at = ?,
                    last_scheduler_check = ?
                WHERE id = ?
            """;
        jdbcClient.sql(sql)
            .param(updatedAt)
            .param(checkedAt)
            .param(id)
            .update();
    }
}
