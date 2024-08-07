package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "relationships")
public class Relationship extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "FK_user-id_users",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "related_user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "FK_related-user-id_users",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonBackReference
    private User relatedUser;

    @ManyToOne
    @JoinColumn(name = "initated_by_user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "FK_initated-by-user-id_users",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonBackReference
    private User initated_by_user;

    @ManyToOne
    @JoinColumn(name = "relationship_type_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "FK_relationship-type_relationships",
                    foreignKeyDefinition = "FOREIGN KEY (relationship_type_id) REFERENCES relationship_types(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonBackReference
    private RelationshipType relationshipType;

    @OneToMany(mappedBy = "relationship", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<RelationshipHistory> relationshipHistories;

    @Column
    String status;

}
