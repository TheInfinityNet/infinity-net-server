package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@FieldDefaults(level=AccessLevel.PRIVATE)
@Table(name = "post_tags")
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post-tags_posts",
                    foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE"), nullable = false)
    @JsonBackReference
    Post post;

    @Column
    String tagId;

}
