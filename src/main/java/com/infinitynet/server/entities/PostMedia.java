package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level=AccessLevel.PRIVATE)
@Table(name = "post_medias")
@PrimaryKeyJoinColumn(name = "id",
        foreignKey = @ForeignKey(
                name = "fk_post_medias_file_metadata",
                foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES file_metadata(id) ON DELETE CASCADE ON UPDATE CASCADE")
)
public class PostMedia extends FileMetadata {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_medias_posts",
                    foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE"), nullable = false)
    @JsonBackReference
    Post post;

}
