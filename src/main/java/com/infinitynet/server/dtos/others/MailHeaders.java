package com.infinitynet.server.dtos.others;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MailHeaders(

        @JsonProperty("X-Mailin-custom")
        String X_Mailin_custom,

        String charset

) {
}
