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
@Table(name = "post_mention_events")
@PrimaryKeyJoinColumn(name = "id",
        foreignKey = @ForeignKey(
                name = "fk_post_mention_events_events",
                foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES events(id) ON DELETE CASCADE ON UPDATE CASCADE")
)
public class PostMentionEvent extends Event {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_mention_events_posts",
                    foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    Post post;

//    @OneToOne
//    @JoinColumn(name = "post_mention_id", referencedColumnName = "id",
//            foreignKey = @ForeignKey(name = "fk_post_mention_events_post_mentions",
//                    foreignKeyDefinition = "FOREIGN KEY (post_mention_id) REFERENCES post_mentions(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
//            updatable = false)
//    @JsonManagedReference
//    PostMention postMention;

}
