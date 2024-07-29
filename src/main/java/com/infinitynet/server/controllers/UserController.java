package com.infinitynet.server.controllers;

import com.infinitynet.server.dtos.requests.UserCreationRequest;
import com.infinitynet.server.dtos.responses.ApiResponse;
import com.infinitynet.server.dtos.responses.UserResponse;
import com.infinitynet.server.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "User APIs")
public class UserController {

    UserService userService;

    @Operation(summary = "Get my info", description = "Get my info",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/my-info")
    ResponseEntity<ApiResponse<UserResponse>> getMyInfo() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<UserResponse>builder().result(userService.getMyInfo()).build());
    }

}