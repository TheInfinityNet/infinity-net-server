package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "post_medias")
@PrimaryKeyJoinColumn(name = "id",
        foreignKey = @ForeignKey(
                name = "fk_post_medias_file_metadata",
                foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES file_metadata(id) ON DELETE CASCADE ON UPDATE CASCADE")
)
public class PostMedia extends FileMetadata {

    public PostMedia(String id, String objectKey, String mediaType, long size, Post post) {
        super(id, objectKey, mediaType, size);
        this.post = post;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_medias_posts",
                    foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE"), nullable = false,
            updatable = false)
    @JsonBackReference
    Post post;

    @OneToMany(mappedBy = "parentPostMedia", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<SharedPost> sharedPosts;

    @OneToMany(mappedBy = "postMedia", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Comment> comments;

    @OneToMany(mappedBy = "postMedia", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<PostMediaReaction> postMediaReactions;

}
