package edu.java.scrapper.domain.jpa;

import edu.java.scrapper.domain.jpa.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTgChatRepository extends JpaRepository<ChatEntity, Long> {

    @Modifying
    @Query(
        value = "truncate table chats restart identity cascade",
        nativeQuery = true
    )
    void truncateTable();
}
