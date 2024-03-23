package edu.java.scrapper.domain.jpa;

import edu.java.scrapper.domain.jpa.entity.LinkEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface JpaLinkRepository extends JpaRepository<LinkEntity, Long> {
    Optional<LinkEntity> findByUrl(String url);

    @Query(value = """
        SELECT *
            FROM links
            WHERE last_scheduler_check IS NULL OR last_scheduler_check <= :lastSchedulerCheck
            ORDER BY last_scheduler_check NULLS FIRST
            LIMIT :linksLimit
        """, nativeQuery = true)
    List<LinkEntity> findAllBeforeLastSchedulerCheck(OffsetDateTime lastSchedulerCheck, long linksLimit);
}
