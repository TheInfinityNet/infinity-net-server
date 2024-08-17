package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.infinitynet.server.enums.PostVisibilityName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "post_visibilities")
public class PostVisibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    PostVisibilityName name;

    @OneToMany(mappedBy = "postVisibility", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Post> posts;

}
