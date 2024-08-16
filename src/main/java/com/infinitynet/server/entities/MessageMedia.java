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
@Table(name = "message_medias")
public class MessageMedia extends AbstractEntity {

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
    @JoinColumn(name = "message_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_message-medias_messages",
                    foreignKeyDefinition = "FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE"), nullable = false)
    @JsonBackReference
    Message message;

//    @ManyToOne
//    @JoinColumn(name = "media_type_id", referencedColumnName = "id",
//            foreignKey = @ForeignKey(
//                    name = "FK_message_media_media_types",
//                    foreignKeyDefinition = "FOREIGN KEY (media_type_id) REFERENCES media_types(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
//    MediaType mediaType;

}