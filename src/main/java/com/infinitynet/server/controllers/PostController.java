package com.infinitynet.server.controllers;

import com.infinitynet.server.dtos.others.Pagination;
import com.infinitynet.server.dtos.requests.post.PostCreationRequest;
import com.infinitynet.server.dtos.responses.CommonResponse;
import com.infinitynet.server.dtos.responses.PaginateResponse;
import com.infinitynet.server.dtos.responses.post.PostReactionResponse;
import com.infinitynet.server.dtos.responses.post.PostResponse;
import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.PostReaction;
import com.infinitynet.server.entities.User;
import com.infinitynet.server.enums.ReactionType;
import com.infinitynet.server.mappers.PostMapper;
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
import org.springframework.data.domain.Page;
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

    PostMapper postMapper = PostMapper.INSTANCE;

    @Operation(summary = "Create a new post", description = "Create a new post with content and media files")
    @PostMapping
    @ResponseStatus(CREATED)
    ResponseEntity<CommonResponse> createPost(@RequestPart @Valid PostCreationRequest request,
                                 @RequestPart(required = false) List<MultipartFile> mediaFiles) {
        SecurityContext context = SecurityContextHolder.getContext();
        User owner = userService.findByEmail(context.getAuthentication().getName());
        Post newPost = postService.createPost(owner, request.content(), request.visibility());

        if (mediaFiles != null && !mediaFiles.isEmpty()) fileService.uploadFiles(newPost, POST, mediaFiles);

        return ResponseEntity.status(CREATED).body(CommonResponse.builder()
                .message(getLocalizedMessage("create_post_success")).build());
    }

    @Operation(summary = "Get post by id", description = "Get a post by id")
    @GetMapping("/{postId}")
    @ResponseStatus(OK)
    ResponseEntity<PostResponse> getPost(@PathVariable String postId) {
        Post post = postService.findById(postId);
        PostResponse response = postMapper.toPostResponse(post);
        response.setReactionCounts(postService.countByPostAndReactionType(post, null));

        return ResponseEntity.status(OK).body(response);
    }

    @Operation(summary = "Get post reactions", description = "Get all reactions to a post")
    @GetMapping("/{postId}/reactions")
    @ResponseStatus(OK)
    ResponseEntity<PaginateResponse<PostReactionResponse>> getReactionsPost(@PathVariable String postId,
                                                      @RequestParam(required = false) ReactionType type,
                                                      @RequestParam(defaultValue = "0") String offset,
                                                      @RequestParam(defaultValue = "100") String limit) {
        Post post = postService.findById(postId);
        Page<PostReaction> reactions =
                postService.findAllByPostAndReactionType(post, type, Integer.parseInt(offset), Integer.parseInt(limit));
        List<PostReactionResponse> items = reactions
                .map(postMapper::toPostReactionResponse)
                .toList();

        return ResponseEntity.status(OK).body(PaginateResponse.<PostReactionResponse>builder()
                .items(items)
                .pagination(new Pagination(Integer.parseInt(offset), Integer.parseInt(limit), reactions.getTotalElements()))
                .build()
        );
    }

    @Operation(summary = "Reaction to a post", description = "Reaction to a post with a reaction type")
    @PostMapping("/{postId}/reactions")
    @ResponseStatus(CREATED)
    ResponseEntity<PostReactionResponse> reactionPost(@PathVariable String postId, @RequestParam ReactionType type) {
        SecurityContext context = SecurityContextHolder.getContext();
        User current = userService.findByEmail(context.getAuthentication().getName());
        Post post = postService.findById(postId);
        PostReaction postReaction = postService.createReactionPost(current, post, type);

        return ResponseEntity.status(CREATED).body(postMapper.toPostReactionResponse(postReaction));
    }

    @Operation(summary = "Delete post reaction", description = "Delete a reaction to a post")
    @DeleteMapping("/{postId}/reactions")
    @ResponseStatus(OK)
    ResponseEntity<PostReactionResponse> deleteReactionPost(@PathVariable String postId) {
        SecurityContext context = SecurityContextHolder.getContext();
        User current = userService.findByEmail(context.getAuthentication().getName());
        Post post = postService.findById(postId);
        PostReaction postReaction = postService.findById(new PostReaction.PostReactionId(current.getId(), post.getId()));
        postService.deleteReactionPost(postReaction);

        return ResponseEntity.status(OK).body(postMapper.toPostReactionResponse(postReaction));
    }


}
