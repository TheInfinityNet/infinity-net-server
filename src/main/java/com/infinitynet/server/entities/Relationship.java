package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.infinitynet.server.enums.RelationshipStatus;
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
@Table(name = "relationships")
public class Relationship extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_relationships_users",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonBackReference
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_related-user",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonBackReference
    User relatedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initated_by_user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_initiated-by-user",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonBackReference
    User initatedByUser;

    @ManyToOne
    @JoinColumn(name = "relationship_type_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_relationships_relationship-types",
                    foreignKeyDefinition = "FOREIGN KEY (relationship_type_id) REFERENCES relationship_types(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonManagedReference
    RelationshipType relationshipType;

    @OneToMany(mappedBy = "relationship", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    List<RelationshipHistory> relationshipHistories;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    RelationshipStatus status;

}
