package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "relationship_histories")
public class RelationshipHistory extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column
    String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship_id", referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk_relationships_relationship-histories",
                foreignKeyDefinition = "FOREIGN KEY (relationship_id) REFERENCES relationships(id) ON DELETE CASCADE"), nullable = false)
    @JsonBackReference
    Relationship relationship;

}
