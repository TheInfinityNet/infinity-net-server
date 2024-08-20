package com.infinitynet.server.dtos.responses.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostMediaResponse {

    String id;

    String url;

    String contentType;

    Long reactionCounts;

    PostReactionResponse currentUserReaction;

    Date createdAt;

    Date updatedAt;

}
