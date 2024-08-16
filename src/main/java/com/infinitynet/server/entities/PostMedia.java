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
@Table(name = "post_medias")
public class PostMedia extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String url;

    @Column(name = "backup_url", nullable = false)
    String backupUrl;

    @Column(nullable = false)
    String mediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post-medias_posts",
                    foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE"), nullable = false)
    @JsonBackReference
    Post post;

}
