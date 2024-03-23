package edu.java.scrapper.domain.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "links")
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "url", nullable = false, length = Integer.MAX_VALUE)
    private String url;

    @Column(name = "last_updated_at")
    private OffsetDateTime lastUpdatedAt;

    @Column(name = "last_scheduler_check")
    private OffsetDateTime lastSchedulerCheck;

    @ManyToMany
    @JoinTable(name = "subscriptions",
               joinColumns = @JoinColumn(name = "link_id"),
               inverseJoinColumns = @JoinColumn(name = "chat_id"))
    private Set<ChatEntity> chats = new LinkedHashSet<>();
}
