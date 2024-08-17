package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level=AccessLevel.PRIVATE)
@Table(name = "comment_mentions")
public class CommentMention extends AbstractEntity {

    @EmbeddedId
    CommentMentionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentId")
    @JoinColumn(name = "comment_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comment_mentions_comments",
                    foreignKeyDefinition = "FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE"), nullable = false)
    @JsonBackReference
    Comment comment;

    @ManyToOne
    @MapsId("mentionedUserId")
    @JoinColumn(name = "mentioned_user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_mentioned_user",
                    foreignKeyDefinition = "FOREIGN KEY (mentioned_user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonManagedReference
    User mentionedUser;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class CommentMentionId implements Serializable {

        @Column(name = "comment_id")
        String commentId;

        @Column(name = "mentioned_user_id")
        String mentionedUserId;

    }

}
