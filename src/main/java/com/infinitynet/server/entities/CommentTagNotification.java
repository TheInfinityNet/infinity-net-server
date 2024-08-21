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
@Table(name = "comment_tag_notifications")
@PrimaryKeyJoinColumn(name = "id",
        foreignKey = @ForeignKey(
                name = "fk_comment_tag_notifications_notifications",
                foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES notifications(id) ON DELETE CASCADE ON UPDATE CASCADE")
)
public class CommentTagNotification extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comment_tag_notifications_comments",
                    foreignKeyDefinition = "FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    Comment comment;

    @OneToOne
    @JoinColumn(name = "comment_tag_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comment_tag_notifications_comment_tags",
                    foreignKeyDefinition = "FOREIGN KEY (comment_tag_id) REFERENCES comment_tags(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    CommentTag commentTag;

}
