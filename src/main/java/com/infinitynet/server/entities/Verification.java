package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.infinitynet.server.enums.VerificationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "verifications")
public class Verification extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String token;

    @Column(length = 6)
    String code;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    Date expiryTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    VerificationType verificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    User user;

}
