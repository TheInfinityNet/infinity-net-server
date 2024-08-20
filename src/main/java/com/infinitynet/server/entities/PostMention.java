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
@Table(name = "post_mentions")
public class PostMention extends AbstractEntity {

    @EmbeddedId
    PostMentionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_mentions_posts",
                    foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE"), nullable = false,
            updatable = false)
    @JsonBackReference
    Post post;

    @ManyToOne
    @MapsId("mentionedUserId")
    @JoinColumn(name = "mentioned_user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_mentioned_user",
                    foreignKeyDefinition = "FOREIGN KEY (mentioned_user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    User mentionedUser;

//    @OneToOne(mappedBy = "postMention", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonBackReference
//    PostMentionEvent postMentionEvent;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class PostMentionId implements Serializable {

        @Column(name = "post_id")
        String postId;

        @Column(name = "mentioned_user_id")
        String mentionedUserId;

    }

}
