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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "post_media_reactions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_media_id", "user_id"})
})
public class PostMediaReaction extends Reaction {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_media_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_media_reactions_posts",
                    foreignKeyDefinition = "FOREIGN KEY (post_media_id) REFERENCES post_medias(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonBackReference
    PostMedia postMedia;

}
