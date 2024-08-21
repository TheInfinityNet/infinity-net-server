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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "message_medias")
@PrimaryKeyJoinColumn(name = "id",
        foreignKey = @ForeignKey(
                name = "fk_message_medias_file_metadata",
                foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES file_metadata(id) ON DELETE CASCADE ON UPDATE CASCADE")
)
public class MessageMedia extends FileMetadata {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_message_medias_messages",
                    foreignKeyDefinition = "FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE"), nullable = false,
            updatable = false)
    @JsonBackReference
    Message message;

}