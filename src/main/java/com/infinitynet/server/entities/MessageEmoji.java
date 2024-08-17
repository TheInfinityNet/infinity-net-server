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
@Table(name = "message_emojis")
public class MessageEmoji extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, length = 50)
    String emojiCode;

    @Column(nullable = false)
    int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_message-emojis_messages",
                    foreignKeyDefinition = "FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE"), nullable = false)
    @JsonBackReference
    Message message;

}
