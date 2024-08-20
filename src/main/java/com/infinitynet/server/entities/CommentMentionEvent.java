package com.infinitynet.server.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comment_mention_events")
@PrimaryKeyJoinColumn(name = "id",
        foreignKey = @ForeignKey(
                name = "fk_comment_mention_events_events",
                foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES events(id) ON DELETE CASCADE ON UPDATE CASCADE")
)
public class CommentMentionEvent extends Event {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comment_mention_events_comments",
                    foreignKeyDefinition = "FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    Comment comment;

    @ManyToOne
    @JoinColumn(name = "mentioned_user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_mentioned_user",
                    foreignKeyDefinition = "FOREIGN KEY (mentioned_user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    User mentionedUser;

//    @OneToOne
//    @JoinColumn(name = "comment_mention_id", referencedColumnName = "id",
//            foreignKey = @ForeignKey(name = "fk_comment_mention_events_comment_mentions",
//                    foreignKeyDefinition = "FOREIGN KEY (comment_mention_id) REFERENCES comment_mentions(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
//            updatable = false)
//    @JsonManagedReference
//    CommentMention commentMention;

}
