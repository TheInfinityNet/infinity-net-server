package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comment_medias")
public class CommentMedia extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "comment_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "FK_comment-meida_comments",
                    foreignKeyDefinition = "FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonBackReference
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "media_type_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "FK_comment_media_media_types",
                    foreignKeyDefinition = "FOREIGN KEY (media_type_id) REFERENCES media_types(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonBackReference
    MediaType mediaType;

    @Column
    String url;


}
