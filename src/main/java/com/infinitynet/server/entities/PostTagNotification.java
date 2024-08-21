package com.infinitynet.server.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(name = "post_tag_notifications")
@PrimaryKeyJoinColumn(name = "id",
        foreignKey = @ForeignKey(
                name = "fk_post_tag_notifications_notifications",
                foreignKeyDefinition = "FOREIGN KEY (id) REFERENCES notifications(id) ON DELETE CASCADE ON UPDATE CASCADE")
)
public class PostTagNotification extends Notification {

    @OneToOne
    @JoinColumn(name = "post_tag_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_tag_notifications_post_tags",
                    foreignKeyDefinition = "FOREIGN KEY (post_tag_id) REFERENCES post_tags(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    PostTag postTag;

}
