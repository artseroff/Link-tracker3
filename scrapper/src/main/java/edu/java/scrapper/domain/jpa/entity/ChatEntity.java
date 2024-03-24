package edu.java.scrapper.domain.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "chats")
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToMany(mappedBy = "chats", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<LinkEntity> links =
        new LinkedHashSet<>();

}
