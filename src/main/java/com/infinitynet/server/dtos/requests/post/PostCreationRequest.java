package com.infinitynet.server.dtos.requests.post;

import com.infinitynet.server.enums.PostVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostCreationRequest (

    @NotNull(message = "null_content")
    @NotBlank(message = "blank_content")
    String content,

    @NotNull(message = "null_visibility")
    PostVisibility visibility

)
{ }
