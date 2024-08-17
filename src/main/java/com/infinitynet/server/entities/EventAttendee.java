package com.infinitynet.server.entities;

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
@Table(name = "event_attendees")
public class EventAttendee extends AbstractEntity {

    @EmbeddedId
    EventAttendeeId id;

    @ManyToOne
    @MapsId("attendeeId")
    @JoinColumn(name = "attendee_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_event_attendees_users",
                    foreignKeyDefinition = "FOREIGN KEY (attendee_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonManagedReference
    User attendee;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_event_attendees_events",
                    foreignKeyDefinition = "FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false)
    @JsonManagedReference
    Event event;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class EventAttendeeId implements Serializable {

        @Column(name = "attendee_id")
        String attendeeId;

        @Column(name = "event_id")
        String eventId;

    }

}
