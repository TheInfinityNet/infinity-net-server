package com.infinitynet.server.controllers;

import com.infinitynet.server.dtos.requests.PostCreationRequest;
import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.PostMedia;
import com.infinitynet.server.entities.User;
import com.infinitynet.server.services.FileService;
import com.infinitynet.server.services.PostService;
import com.infinitynet.server.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.infinitynet.server.components.Translator.getLocalizedMessage;
import static com.infinitynet.server.enums.MediaOwnerType.POST;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${api.prefix}/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Post APIs")
public class PostController {

    PostService postService;

    FileService fileService;

    UserService userService;

    @Operation(summary = "Create a new post")
    @PostMapping
    ResponseEntity<?> createPost(@RequestPart(name = "request") @Valid PostCreationRequest request,
                                 @RequestPart(name = "mediaFiles", required = false) List<MultipartFile> mediaFiles) {

        SecurityContext context = SecurityContextHolder.getContext();
        User owner = userService.findByEmail(context.getAuthentication().getName());
        Post newPost = postService.createPost(owner, request.content(), request.visibility());
        fileService.uploadFiles(newPost, POST, mediaFiles);
        return ResponseEntity.status(CREATED).body(getLocalizedMessage("create_post_success"));
    }


}
