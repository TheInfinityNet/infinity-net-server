package com.infinitynet.server.dtos.responses;

import com.infinitynet.server.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoResponse {

    String id;

    String userName;

    String firstName;

    String lastName;

    String middleName;

}