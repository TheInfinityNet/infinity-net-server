package com.infinitynet.server.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(name = "post_reaction_notifications")
@PrimaryKeyJoinColumn(name = "id",
        foreignKey = @ForeignKey(
                name = "fk_post_reaction_notifications_notifications",
                foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES notifications(id) ON DELETE CASCADE ON UPDATE CASCADE")
)
public class PostReactionNotification extends Notification {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_reaction_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_reaction_notifications_post_reactions",
                    foreignKeyDefinition = "FOREIGN KEY (post_reaction_id) REFERENCES post_reactions(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    PostReaction postReaction;

}
