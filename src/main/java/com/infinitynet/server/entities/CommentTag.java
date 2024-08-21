package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level=AccessLevel.PRIVATE)
@Table(name = "comment_tags", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"comment_id", "tagged_user_id"})
})
public class CommentTag extends Tag {

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentId")
    @JoinColumn(name = "comment_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comment_tags_comments",
                    foreignKeyDefinition = "FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE"), nullable = false,
            updatable = false)
    @JsonBackReference
    Comment comment;

    @OneToOne(mappedBy = "commentTag", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    CommentTagNotification commentTagNotification;

}
