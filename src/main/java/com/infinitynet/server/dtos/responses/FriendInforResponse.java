package com.infinitynet.server.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendInforResponse {
    String id;

    String userName;

    String firstName;

    String lastName;

    String middleName;

    String avatar;
}
