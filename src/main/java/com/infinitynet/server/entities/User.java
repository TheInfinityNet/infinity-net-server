package com.infinitynet.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.infinitynet.server.enums.Gender;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(unique = true, nullable = false, length = 100)
    String email;

    @Column(nullable = false)
    String password;

    @Column(name = "is_activated", nullable = false)
    boolean isActivated;

    String bio;

    String avatar;

    String cover;

    @Column(name = "user_name", nullable = false, unique = true)
    String userName;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(name = "last_name", nullable = false)
    String lastName;

    @Column(name = "middle_name")
    String middleName;

    @Column(name = "mobile_number")
    String mobileNumber;

    @Column(name = "birthdate")
    LocalDate birthdate;

    @Column(name = "accept_terms", nullable = false)
    boolean acceptTerms;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Gender gender;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Verification> verifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Setting> settings;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Event> events;

    @OneToMany(mappedBy = "attendee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<EventAttendee> eventAttendees;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Notification> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<SearchingHistory> searchingHistories;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Message> receivedMessages;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<MessageEmoji> messageEmojis;

    @OneToMany(mappedBy = "initiatedByUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Relationship> relationshipsWhereInitiatedBy;

    @OneToMany(mappedBy = "relatedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Relationship> relationshipsWhereRelated;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<PostReaction> postReactions;

    @OneToMany(mappedBy = "mentionedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<PostMention> postMentions;

    @OneToMany(mappedBy = "mentionedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<PostMentionEvent> postMentionEvents;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<CommentReaction> commentReactions;

    @OneToMany(mappedBy = "mentionedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<CommentMentionEvent> commentMentionEvents;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    List<AuditLog> auditLogs;

}