package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@FieldDefaults(level=AccessLevel.PRIVATE)
@Table(name = "message_emojis")
public class MessageEmoji extends AbstractEntity {

    @EmbeddedId
    MessageEmojiId id;

    @Column(nullable = false, length = 50)
    String emojiCode;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_message_emojis_users",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageId")
    @JoinColumn(name = "message_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_message_emojis_messages",
                    foreignKeyDefinition = "FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE"), nullable = false,
            updatable = false)
    @JsonBackReference
    Message message;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class MessageEmojiId implements Serializable {

        @Column(name = "user_id")
        String userId;

        @Column(name = "message_id")
        String messageId;

    }

}
