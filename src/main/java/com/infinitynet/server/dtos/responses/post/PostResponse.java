package com.infinitynet.server.dtos.responses.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {

    String id;

    String userId;

    String content;

    Long reactionCounts;

    PostReactionResponse currentUserReaction;

    Date createdAt;

    Date updatedAt;

    List<PostMediaResponse> medias;

}
