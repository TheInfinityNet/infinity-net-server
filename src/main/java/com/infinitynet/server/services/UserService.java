package com.infinitynet.server.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.github.javafaker.Faker;
import com.infinitynet.server.dtos.requests.UserCreationRequest;
import com.infinitynet.server.dtos.responses.UserResponse;
import com.infinitynet.server.entities.User;
import com.infinitynet.server.exceptions.AppException;
import com.infinitynet.server.exceptions.ErrorCode;
import com.infinitynet.server.mappers.UserMapper;
import com.infinitynet.server.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import static com.infinitynet.server.exceptions.ErrorCode.USER_EXISTED;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper = UserMapper.INSTANCE;
    PasswordEncoder passwordEncoder;

    @PostConstruct
    public void generateAndSaveFakeUsers() {
        Faker faker = new Faker();
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) { // Generate 10 fake users
            String email = faker.internet().emailAddress();
            String password = passwordEncoder.encode(email);
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setBio("I like " + faker.animal().name());
            user.setProfilePictureUrl(faker.internet().domainName());
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            users.add(user);
        }
        userRepository.saveAll(users);
    }

    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            user = userRepository.save(user);

        } catch (DataIntegrityViolationException exception) {
            throw new AppException(USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }

    @PostAuthorize("returnObject.email == authentication.name") // CHECK OWNER
    public UserResponse getMyInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

}