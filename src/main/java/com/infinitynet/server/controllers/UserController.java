package com.infinitynet.server.controllers;

import com.infinitynet.server.dtos.others.Pagination;
import com.infinitynet.server.dtos.responses.FriendInforResponse;
import com.infinitynet.server.dtos.responses.PaginateResponse;
import com.infinitynet.server.dtos.responses.UserInfoResponse;
import com.infinitynet.server.dtos.responses.post.PostMediaResponse;
import com.infinitynet.server.dtos.responses.post.PostResponse;
import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.PostMedia;
import com.infinitynet.server.entities.PostReaction;
import com.infinitynet.server.entities.User;
import com.infinitynet.server.exceptions.post.PostException;
import com.infinitynet.server.mappers.PostMapper;
import com.infinitynet.server.services.FileService;
import com.infinitynet.server.services.PostService;
import com.infinitynet.server.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

    PostService postService;

    FileService<Post, PostMedia> fileService;

    PostMapper postMapper = PostMapper.INSTANCE;

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

    @Operation(summary = "Get user posts", description = "Get user posts")
    @GetMapping("/{userId}/posts")
    @ResponseStatus(OK)
    ResponseEntity<PaginateResponse<PostResponse>> getUserPosts(@PathVariable String userId,
                                                               @RequestParam(defaultValue = "0") String offset,
                                                               @RequestParam(defaultValue = "100") String limit) {
        User current = userService.findById(userId);
        Page<Post> posts = postService.findAllByUser(current, Integer.parseInt(offset), Integer.parseInt(limit));
        List<PostResponse> items = posts
                .map(post -> {
                    PostReaction currentUsersReaction;
                    try {
                        currentUsersReaction = postService.findById(new PostReaction.PostReactionId(current.getId(), post.getId()));
                    } catch (PostException e) {
                        currentUsersReaction = null;
                    }

                    List<PostMediaResponse> medias = postService.previewMedias(post)
                            .stream()
                            .map(media -> {
                                PostMediaResponse dto = postMapper.toPostMediaResponse(media);
                                String url = fileService.getObjectUrl(media);
                                dto.setUrl(url);
                                return dto;
                            })
                            .toList();

                    PostResponse response = postMapper.toPostResponse(post);
                    response.setReactionCounts(postService.countByPostAndReactionType(post, null));
                    response.setCurrentUserReaction(postMapper.toPostReactionResponse(currentUsersReaction));
                    response.setMedias(medias);
                    return response;
                })
                .toList();

        return ResponseEntity.status(OK).body(PaginateResponse.<PostResponse>builder()
                .items(items)
                .pagination(new Pagination(Integer.parseInt(offset), Integer.parseInt(limit), posts.getTotalElements()))
                .build()
        );
    }

}