package edu.java.scrapper.domain.jpa.entity;

import edu.java.scrapper.configuration.db.UriAttributeConverter;
import edu.java.scrapper.domain.dto.TimeDifferenceUtils;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "links")
@AllArgsConstructor
@NoArgsConstructor
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Convert(converter = UriAttributeConverter.class)
    @NotNull
    @Column(name = "url", nullable = false, length = Integer.MAX_VALUE)
    private URI url;

    @Column(name = "last_updated_at")
    private OffsetDateTime lastUpdatedAt;

    @Column(name = "last_scheduler_check")
    private OffsetDateTime lastSchedulerCheck;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "subscriptions",
               joinColumns = @JoinColumn(name = "link_id"),
               inverseJoinColumns = @JoinColumn(name = "chat_id"))
    private Set<ChatEntity> chats = new LinkedHashSet<>();

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LinkEntity that = (LinkEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(url, that.url)
            && TimeDifferenceUtils.isTimeEqualWithEpsilon(lastUpdatedAt, that.lastUpdatedAt)
            && TimeDifferenceUtils.isTimeEqualWithEpsilon(lastSchedulerCheck, that.lastSchedulerCheck)
            && Objects.equals(chatsToIds(chats), chatsToIds(that.chats));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, lastUpdatedAt, lastSchedulerCheck, chatsToIds(chats));
    }

    private static List<Long> chatsToIds(Set<ChatEntity> chats) {
        return chats.stream()
            .map(ChatEntity::getId)
            .toList();
    }

    public void addChat(ChatEntity chatEntity) {
        chatEntity.getLinks().add(this);
        chats.add(chatEntity);
    }

    public void removeChat(ChatEntity chatEntity) {
        chatEntity.getLinks().remove(this);
        chats.remove(chatEntity);
    }
}
