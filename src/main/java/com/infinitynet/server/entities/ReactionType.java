package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.infinitynet.server.enums.ReactionTypeName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@FieldDefaults(level=AccessLevel.PRIVATE)
@Table(name = "reaction_types")
public class ReactionType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @OneToMany(mappedBy = "reactionType", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<CommentReaction> commentReactions;

    @OneToMany(mappedBy = "reactionType", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<PostReaction> postReactions;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ReactionTypeName name;

}
