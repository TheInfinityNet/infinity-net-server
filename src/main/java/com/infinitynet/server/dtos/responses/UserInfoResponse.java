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

    String avatar;

    String cover;

    String email;

    String bio;

    String username;

    String firstName;

    String lastName;

    String middleName;

    String mobileNumber;

    LocalDate birthDate;

    boolean acceptTerms;

    Gender gender;

}