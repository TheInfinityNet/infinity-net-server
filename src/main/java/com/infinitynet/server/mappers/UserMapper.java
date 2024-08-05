package com.infinitynet.server.mappers;

import com.infinitynet.server.dtos.requests.authentication.SignUpRequest;
import com.infinitynet.server.dtos.responses.UserInfoResponse;
import com.infinitynet.server.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(SignUpRequest request);

    UserInfoResponse toUserInfoResponse(User user);

}