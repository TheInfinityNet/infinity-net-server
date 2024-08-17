package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
                    foreignKeyDefinition = "FOREIGN KEY (parent_post_id) REFERENCES posts(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonManagedReference
    Post parentPost;

}
