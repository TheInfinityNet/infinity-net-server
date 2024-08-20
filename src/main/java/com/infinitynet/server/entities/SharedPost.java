package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.infinitynet.server.enums.SharedPostType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "shared_posts")
@PrimaryKeyJoinColumn(name = "id",
        foreignKey = @ForeignKey(
                name = "fk_shared_posts_posts",
                foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES posts(id) ON DELETE CASCADE ON UPDATE CASCADE")
)
public class SharedPost extends Post {

    @ManyToOne
    @JoinColumn(name = "parent_post_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_parent_post",
                    foreignKeyDefinition = "FOREIGN KEY (parent_post_id) REFERENCES posts(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    Post parentPost;

    @ManyToOne
    @JoinColumn(name = "parent_post_media_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_parent_post_media",
                    foreignKeyDefinition = "FOREIGN KEY (parent_post_media_id) REFERENCES post_medias(id) ON DELETE CASCADE ON UPDATE CASCADE"),
            updatable = false)
    @JsonManagedReference
    PostMedia parentPostMedia;

    @Column(name = "shared_post_type", nullable = false)
    SharedPostType sharedPostType;

    @Column(name = "shared_to", nullable = false)
    String sharedTo;

}
