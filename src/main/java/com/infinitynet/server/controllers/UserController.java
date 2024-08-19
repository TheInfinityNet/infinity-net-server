package com.infinitynet.server.controllers;

import com.infinitynet.server.dtos.responses.FriendInforResponse;
import com.infinitynet.server.dtos.responses.UserInfoResponse;
import com.infinitynet.server.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "User APIs")
public class UserController {

    UserService userService;

    @Operation(summary = "Get user profile", description = "Get user profile",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{userId}")
    ResponseEntity<UserInfoResponse> getProfile(@PathVariable String userId) {
        return ResponseEntity.status(OK).body(userService.getUserInfo(userId));
    }

    @Operation(summary = "Get friends", description = "Get friends")
    @GetMapping("/{userId}/friends")
    ResponseEntity<List<FriendInforResponse>> getFriends(@PathVariable String userId, @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                         @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.status(OK).body(userService.getFriends(userId, offset, limit));
    }
//    @Operation(summary = "Get friend pending requests", description = "Get friend pending requests")
//    @GetMapping("/{userId}/friends/pending-requests")
//    ResponseEntity<Map<List<FriendInforResponse>, Pagination>> getFriendRequests(@PathVariable String userId, @RequestParam(name = "offset", defaultValue = "0") int offset,
//                                                                                 @RequestParam(name = "limit", defaultValue = "10") int limit) {
//        Map<List<FriendInforResponse>, Pagination> response = new HashMap<>();
//        response.put(userService.getFriendRequests(userId, offset, limit), new Pagination(offset, limit, offset + limit, offset - limit, 0, 0, 0));
//        return ResponseEntity.status(OK).body(userService.getFriends(userId, offset, limit));
//    }
}