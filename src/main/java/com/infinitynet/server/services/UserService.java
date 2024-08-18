package com.infinitynet.server.services;

import com.infinitynet.server.dtos.responses.FriendInforResponse;
import com.infinitynet.server.dtos.responses.UserInfoResponse;
import com.infinitynet.server.entities.User;

import java.util.List;

public interface UserService {

    List<FriendInforResponse> getFriends(String userId, int offset, int limit);

    UserInfoResponse getMyInfo();

    UserInfoResponse getUserInfo(String userId);

    User findByEmail(String email);

    User findById(String id);

    boolean existsByEmail(String email);

    User createUser(User user);

    void updatePassword(User user, String password);

    void activateUser(User user);

}