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
@Table(name = "post_tags", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "tagged_user_id"})
})
public class PostTag extends Tag {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_tags_posts",
                    foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE"), nullable = false,
            updatable = false)
    @JsonBackReference
    Post post;

    @OneToOne(mappedBy = "postTag", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    PostTagNotification postTagNotification;

}
