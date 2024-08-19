package com.infinitynet.server.dtos.responses.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.infinitynet.server.enums.ReactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostReactionResponse {

        String postId;

        String userId;

        @JsonProperty("type")
        ReactionType reactionType;

        Date createdAt;

        Date updatedAt;

}
