package com.infinitynet.server.entities;

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
@Table(name = "message_medias")
public class MessageMedia extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column
    String messageId;

    @Column
    String url;

    @ManyToOne
    @JoinColumn(name = "media_type_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "FK_message_media_media_types",
                    foreignKeyDefinition = "FOREIGN KEY (media_type_id) REFERENCES media_types(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    MediaType mediaType;
}