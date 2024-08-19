package com.infinitynet.server.dtos.requests.post;

import com.infinitynet.server.enums.ReactionType;
import jakarta.validation.constraints.NotNull;

public record PostReactionCreationRequest (

    @NotNull(message = "null_reaction_type")
    ReactionType reactionType

)
{ }
