package com.infinitynet.server.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FileUploadRequest (

    @NotNull(message = "null_owner_id")
    @NotBlank(message = "blank_owner_id")
    String ownerId

)
{ }
