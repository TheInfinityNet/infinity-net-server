package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.infinitynet.server.enums.SettingKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "settings")
public class Setting extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_settings_users",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE"), nullable = false,
            updatable = false)
    @JsonManagedReference
    User user;

    @Column(name = "setting_key", nullable = false)
    @Enumerated(EnumType.STRING)
    SettingKey settingKey;

    @Column(name = "setting_value", nullable = false)
    String settingValue;

}