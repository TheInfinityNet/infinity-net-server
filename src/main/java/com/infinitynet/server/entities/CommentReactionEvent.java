package com.infinitynet.server.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.infinitynet.server.enums.ReactionType;
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
@Table(name = "comment_reaction_events")
@PrimaryKeyJoinColumn(name = "id",
        foreignKey = @ForeignKey(
                name = "fk_comment_reaction_events_events",
                foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES events(id) ON DELETE CASCADE ON UPDATE CASCADE")
)
public class CommentReactionEvent extends Event {

    @Column(name = "reaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    ReactionType reactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comment_reaction_events_comments",
                    foreignKeyDefinition = "FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonManagedReference
    Comment comment;

}
