package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level=AccessLevel.PRIVATE)
@Table(name = "notification_consumers")
public class NotificationConsumer extends AbstractEntity {

    @EmbeddedId
    NotificationConsumerId id;

    @ManyToOne
    @MapsId("consumerId")
    @JoinColumn(name = "consumer_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_consumers",
                    foreignKeyDefinition = "FOREIGN KEY (consumer_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    User consumer;

    @ManyToOne
    @MapsId("notificationId")
    @JoinColumn(name = "notification_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_notification_consumers_notifications",
                    foreignKeyDefinition = "FOREIGN KEY (notification_id) REFERENCES notifications(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    Notification notification;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class NotificationConsumerId implements Serializable {

        @Column(name = "consumer_id")
        String consumerId;

        @Column(name = "notification_id")
        String notificationId;

    }

}
