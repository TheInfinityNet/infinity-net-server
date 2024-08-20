package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.infinitynet.server.enums.RelationshipStatus;
import com.infinitynet.server.enums.RelationshipType;
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
@Table(name = "relationships")
public class Relationship extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_related_user",
                    foreignKeyDefinition = "FOREIGN KEY (related_user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonBackReference
    User relatedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiated_by_user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_initiated_by_user",
                    foreignKeyDefinition = "FOREIGN KEY (initiated_by_user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonBackReference
    User initiatedByUser;

    @Column(name = "relationship_type", nullable = false)
    @Enumerated(EnumType.STRING)
    RelationshipType relationshipType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    RelationshipStatus status;

}
