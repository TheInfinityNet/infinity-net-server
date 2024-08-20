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
@Table(name = "post_reaction_events")
@PrimaryKeyJoinColumn(name = "id",
        foreignKey = @ForeignKey(
                name = "fk_post_reaction_events_events",
                foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES events(id) ON DELETE CASCADE ON UPDATE CASCADE")
)
public class PostReactionEvent extends Event {

    @Column(name = "reaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    ReactionType reactionType;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "post_reaction_id", referencedColumnName = "id",
//            foreignKey = @ForeignKey(name = "fk_post_reaction_events_post_reactions",
//                    foreignKeyDefinition = "FOREIGN KEY (post_reaction_id) REFERENCES post_reactions(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
//            updatable = false)
//    @JsonManagedReference
//    PostReaction postReaction;

}
