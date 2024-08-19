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
public class PostResponse {

    String id;

    String userId;

    String content;

    Long reactionCounts;

    Date createdAt;

    Date updatedAt;

}
