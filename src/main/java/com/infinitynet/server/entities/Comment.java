package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comments")
public class Comment extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column
    String content;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comments_posts",
                    foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE"),
            updatable = false)
    @JsonManagedReference
    Post post;

    @ManyToOne
    @JoinColumn(name = "post_media_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comments_post_medias",
                    foreignKeyDefinition = "FOREIGN KEY (post_media_id) REFERENCES post_medias(id) ON DELETE CASCADE"),
            updatable = false)
    @JsonManagedReference
    PostMedia postMedia;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comments_users",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_parent_comment",
                    foreignKeyDefinition = "FOREIGN KEY (parent_comment_id) REFERENCES users(id) ON DELETE CASCADE"), updatable = false)
    @JsonBackReference
    Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<Comment> subComments;

    @OneToOne(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    CommentMedia commentMedia;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<CommentTag> commentTags;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<CommentReaction> commentReactions;

}
