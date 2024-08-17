package com.infinitynet.server.services.impls;

import com.github.javafaker.Faker;
import com.infinitynet.server.dtos.responses.UserInfoResponse;
import com.infinitynet.server.entities.User;
import com.infinitynet.server.enums.Gender;
import com.infinitynet.server.exceptions.authentication.AuthenticationException;
import com.infinitynet.server.mappers.UserMapper;
import com.infinitynet.server.repositories.UserRepository;
import com.infinitynet.server.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode.USER_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    UserMapper userMapper = UserMapper.INSTANCE;

    PasswordEncoder passwordEncoder;

//    @PostConstruct
//    public void generateAndSaveFakeUsers() {
//        Faker faker = new Faker();
//        List<User> users = new ArrayList<>();
//        for (int i = 0; i < 10; i++) { // Generate 10 fake users
//            String email = faker.internet().emailAddress();
//            String password = passwordEncoder.encode(email);
//            User user = new User();
//            user.setEmail(email);
//            user.setUserName(faker.name().username());
//            user.setPassword(password);
//            user.setFirstName(faker.name().firstName());
//            user.setLastName(faker.name().lastName());
//            user.setGender(Gender.OTHER);
//            users.add(user);
//        }
//        userRepository.saveAll(users);
//    }

    @Override
    @PostAuthorize("returnObject.email == authentication.name") // CHECK OWNER
    public UserInfoResponse getMyInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = findByEmail(email);
        return userMapper.toUserInfoResponse(user);
    }

    @Override
    public UserInfoResponse getUserInfo(String userId) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new AuthenticationException(USER_NOT_FOUND, NOT_FOUND));

        return userMapper.toUserInfoResponse(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new AuthenticationException(USER_NOT_FOUND, NOT_FOUND));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void updatePassword(User user, String password) {
        user.setPassword(password);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void activateUser(User user) {
        user.setActivated(true);
        userRepository.save(user);
    }

}