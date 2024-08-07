package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "relationship_history")
public class RelationshipHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column
    String status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    Date changedAt;

    @ManyToOne
    @JoinColumn(name = "relationship_id", referencedColumnName = "id",
        foreignKey = @ForeignKey(
                name = "FK_relationship-histories_relationships",
                foreignKeyDefinition = "FOREIGN KEY (relationship_id) REFERENCES relationships(id) ON DELETE CASCADE"), nullable = false)
    Relationship relationship;
}
