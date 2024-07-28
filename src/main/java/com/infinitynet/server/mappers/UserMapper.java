package com.infinitynet.server.mappers;

import com.infinitynet.server.dtos.requests.UserCreationRequest;
import com.infinitynet.server.dtos.responses.UserResponse;
import com.infinitynet.server.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

}