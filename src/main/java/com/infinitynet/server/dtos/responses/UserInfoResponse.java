package com.infinitynet.server.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

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

    String userName;

    String firstName;

    String lastName;

    String middleName;

    String mobileNumber;

    LocalDate birthDate;

    boolean acceptTerms;

    String gender;

}