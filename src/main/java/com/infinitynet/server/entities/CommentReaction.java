package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comment_reactions")
public class CommentReaction extends AbstractEntity {

    @EmbeddedId
    CommentReactionId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comment-reactions_users",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonManagedReference
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentId")
    @JoinColumn(name = "comment_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comment-reactions_comments",
                    foreignKeyDefinition = "FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonBackReference
    Comment comment;

    @ManyToOne
    @MapsId("reactionTypeId")
    @JoinColumn(name = "reaction_type_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comment-reactions_reaction-types",
                    foreignKeyDefinition = "FOREIGN KEY (reaction_type_id) REFERENCES reaction_types(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonManagedReference
    ReactionType reactionType;

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

        @Column(name = "reaction_type_id")
        String reactionTypeId;

    }

}
