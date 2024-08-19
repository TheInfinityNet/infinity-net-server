package com.infinitynet.server.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.infinitynet.server.enums.ReactionType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostReactionCreationResponse (

        String postId,

        UserInfoResponse user,

        ReactionType reactionType

) {
}
