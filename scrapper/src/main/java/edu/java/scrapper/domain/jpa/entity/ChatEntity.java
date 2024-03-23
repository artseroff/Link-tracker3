package edu.java.scrapper.domain.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "chats")
public class ChatEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    /*@ManyToMany
    @JoinTable(name = "subscriptions",
               joinColumns = @JoinColumn(name = "chat_id"),
               inverseJoinColumns = @JoinColumn(name = "link_id"))*/

    @ManyToMany(mappedBy = "chats")
    private Set<LinkEntity> links =
        new LinkedHashSet<>();

}
