package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comment_reactions")
public class CommentReaction extends AbstractEntity{
    @EmbeddedId
    CommentReactionId Id;

    @ManyToOne
    @JoinColumn(name = "reaction_type_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "FK_reaction-types_comment-reactions",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    ReactionType reactionType;
//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id",
//            foreignKey = @ForeignKey(
//                    name = "FK_notifications_users",
//                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
//    @JsonManagedReference
//    User user;
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class CommentReactionId implements Serializable {
        @Column(name = "user_id")
        String userId;
        @Column(name = "comment_id")
        String commentId;
    }
}
