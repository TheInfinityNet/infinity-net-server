package com.infinitynet.server.controllers;

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
    ResponseEntity<?> getProfile(@PathVariable String userId) {
        return ResponseEntity.status(OK).body(userService.getUserInfo(userId));
    }

}