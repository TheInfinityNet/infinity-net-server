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
@Table(name = "comment_reaction_notifications")
@PrimaryKeyJoinColumn(name = "id",
        foreignKey = @ForeignKey(
                name = "fk_comment_reaction_notifications_notifications",
                foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES notifications(id) ON DELETE CASCADE ON UPDATE CASCADE")
)
public class CommentReactionNotification extends Notification {

    @OneToOne
    @JoinColumn(name = "comment_reaction_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comment_reaction_notifications_comment_reactions",
                    foreignKeyDefinition = "FOREIGN KEY (comment_reaction_id) REFERENCES comment_reactions(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    CommentReaction commentReaction;

}
