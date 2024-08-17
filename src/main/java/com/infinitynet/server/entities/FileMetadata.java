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
@Table(name = "file_metadata")
@Inheritance(strategy = InheritanceType.JOINED)
public class FileMetadata extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "object_key", nullable = false)
    String objectKey;

    @Column(name = "backup_url", nullable = false)
    String backupUrl;

    @Column(name = "media_type", nullable = false)
    String mediaType;

    @Column(nullable = false)
    double size;

}
