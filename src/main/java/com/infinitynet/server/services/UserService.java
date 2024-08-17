package com.infinitynet.server.services;

import com.infinitynet.server.dtos.responses.UserInfoResponse;
import com.infinitynet.server.entities.User;

public interface UserService {

    UserInfoResponse getMyInfo();

    UserInfoResponse getUserInfo(String userId);

    User findByEmail(String email);

    boolean existsByEmail(String email);

    User createUser(User user);

    void updatePassword(User user, String password);

    void activateUser(User user);

}