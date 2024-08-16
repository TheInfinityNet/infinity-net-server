package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.infinitynet.server.enums.ReactionTypeName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "relationship_types")
public class RelationshipType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ReactionTypeName name;

    String description;

    @OneToMany(mappedBy = "relationshipType", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Relationship> relationships;

}
