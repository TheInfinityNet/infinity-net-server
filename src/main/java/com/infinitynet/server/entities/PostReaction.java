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
@Table(name = "post_reactions")
public class PostReaction extends AbstractEntity {

    @EmbeddedId
    PostReactionId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post-reactions_users",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonManagedReference
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post-reactions_posts",
                    foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonBackReference
    Post post;

    @ManyToOne
    @MapsId("reactionTypeId")
    @JoinColumn(name = "reaction_type_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post-reactions_reaction-types",
                    foreignKeyDefinition = "FOREIGN KEY (reaction_type_id) REFERENCES reaction_types(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonManagedReference
    ReactionType reactionType;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class PostReactionId implements Serializable {

        @Column(name = "user_id")
        String userId;

        @Column(name = "post_id")
        String postId;

        @Column(name = "reaction_type_id")
        Integer reactionTypeId;

    }

}
