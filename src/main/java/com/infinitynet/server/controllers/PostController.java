package com.infinitynet.server.controllers;

import com.infinitynet.server.dtos.others.Pagination;
import com.infinitynet.server.dtos.requests.post.PostCreationRequest;
import com.infinitynet.server.dtos.requests.post.PostUpdateRequest;
import com.infinitynet.server.dtos.responses.CommonResponse;
import com.infinitynet.server.dtos.responses.PaginateResponse;
import com.infinitynet.server.dtos.responses.post.PostMediaResponse;
import com.infinitynet.server.dtos.responses.post.PostReactionResponse;
import com.infinitynet.server.dtos.responses.post.PostResponse;
import com.infinitynet.server.entities.*;
import com.infinitynet.server.enums.ReactionType;
import com.infinitynet.server.mappers.PostMapper;
import com.infinitynet.server.services.FileService;
import com.infinitynet.server.services.PostService;
import com.infinitynet.server.services.ReactionService;
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
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${api.prefix}/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Post APIs")
public class PostController {

    PostService postService;

    ReactionService<Post, PostReaction> postReactionService;

    ReactionService<PostMedia, PostMediaReaction> postMediaReactionService;

    FileService<Post, PostMedia> fileService;

    UserService userService;

    PostMapper postMapper = PostMapper.INSTANCE;

    /*_____________________________________________________POST__________________________________________________________*/
    @Operation(summary = "Create a new post", description = "Create a new post with content, privacy setting and media files")
    @PostMapping
    @ResponseStatus(CREATED)
    ResponseEntity<CommonResponse<?>> createPost(@RequestPart @Valid PostCreationRequest request,
                                 @RequestPart(required = false) List<MultipartFile> mediaFiles) {
        SecurityContext context = SecurityContextHolder.getContext();
        User owner = userService.findByEmail(context.getAuthentication().getName());
        Post newPost = postService.createPost(owner, request.content(), null, request.privacySetting());

        if (mediaFiles != null && !mediaFiles.isEmpty()) fileService.uploadFiles(newPost, mediaFiles);

        return ResponseEntity.status(CREATED).body(CommonResponse.builder()
                .message(getLocalizedMessage("create_post_success", newPost.getId())).build());
    }

    @Operation(summary = "Update a post", description = "Update a post with new content, privacy setting and media files")
    @PutMapping("/{postId}")
    @ResponseStatus(OK)
    ResponseEntity<CommonResponse<?>> updatePost(@PathVariable String postId,
                                 @RequestPart @Valid PostUpdateRequest request,
                                 @RequestPart(required = false) List<MultipartFile> additionalFiles) {
        Post post = postService.findById(postId);
        postService.updatePost(postId, request.content(), request.privacySetting());

        if (request.deletedFiles() != null && !request.deletedFiles().isEmpty())
            request.deletedFiles().forEach(fileService::deleteFile);

        if (additionalFiles != null && !additionalFiles.isEmpty()) fileService.uploadFiles(post, additionalFiles);

        return ResponseEntity.status(OK).body(CommonResponse.builder()
                .message(getLocalizedMessage("update_post_success", postId)).build());
    }

    @Operation(summary = "Delete a post", description = "Delete a post by id")
    @DeleteMapping("/{postId}")
    @ResponseStatus(OK)
    ResponseEntity<CommonResponse<?>> deletePost(@PathVariable String postId) {
        Post deletedPost = postService.findById(postId);
        postService.deletePost(deletedPost);
        fileService.deleteFolder(deletedPost);
        return ResponseEntity.status(OK).body(CommonResponse.builder()
                .message(getLocalizedMessage("delete_post_success", postId)).build());
    }

    @Operation(summary = "Get news feed", description = "Get news feed of the current user")
    @GetMapping("/news-feed")
    @ResponseStatus(OK)
    ResponseEntity<PaginateResponse<PostResponse>> getNewsFeed(@RequestParam(defaultValue = "0") String offset,
                                                   @RequestParam(defaultValue = "100") String limit) {
        SecurityContext context = SecurityContextHolder.getContext();
        User current = userService.findByEmail(context.getAuthentication().getName());
        Page<Post> posts = postService.findAll(Integer.parseInt(offset), Integer.parseInt(limit));
        List<PostResponse> items = posts
                .map(post -> {
                    PostReaction currentUsersReaction = postReactionService.getCurrentUserReaction(post, current);
                    List<PostMediaResponse> medias = getPostMedias(post)
                            .stream()
                            .map(media -> {
                                PostMediaResponse dto = postMapper.toPostMediaResponse(media);
                                String url = fileService.getObjectUrl(media);
                                dto.setUrl(url);
                                return dto;
                            })
                            .toList();

                    PostResponse response = postMapper.toPostResponse(post);
                    response.setReactionCounts(postReactionService.countByOwnerAndReactionType(post, null));
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

    @Operation(summary = "Get post by id", description = "Get a post by id")
    @GetMapping("/{postId}")
    @ResponseStatus(OK)
    ResponseEntity<PostResponse> getPost(@PathVariable String postId) {
        SecurityContext context = SecurityContextHolder.getContext();
        User current = userService.findByEmail(context.getAuthentication().getName());
        Post post = postService.findById(postId);

        PostReaction currentUsersReaction = postReactionService.getCurrentUserReaction(post, current);
        List<PostMediaResponse> medias = getPostMedias(post)
                .stream()
                .map(media -> {
                    PostMediaResponse dto = postMapper.toPostMediaResponse(media);
                    String url = fileService.getObjectUrl(media);
                    PostMediaReaction currentUsersMediaReaction =
                            postMediaReactionService.getCurrentUserReaction(media, current);
                    dto.setUrl(url);
                    dto.setCurrentUserReaction(postMapper.toPostReactionResponse(currentUsersMediaReaction));
                    dto.setReactionCounts(postMediaReactionService.countByOwnerAndReactionType(media, null));
                    return dto;
                })
                .toList();

        PostResponse response = postMapper.toPostResponse(post);
        response.setReactionCounts(postReactionService.countByOwnerAndReactionType(post, null));
        response.setCurrentUserReaction(postMapper.toPostReactionResponse(currentUsersReaction));
        response.setMedias(medias);
        return ResponseEntity.status(OK).body(response);
    }

    /*_____________________________________________________GET-REACTIONS__________________________________________________________*/
    @Operation(summary = "Get post reactions", description = "Get reactions of a post")
    @GetMapping("/{postId}/reactions")
    @ResponseStatus(OK)
    ResponseEntity<PaginateResponse<PostReactionResponse>> getPostReactions(@PathVariable String postId,
                                                      @RequestParam(required = false) ReactionType type,
                                                      @RequestParam(defaultValue = "0") String offset,
                                                      @RequestParam(defaultValue = "100") String limit) {
        Post post = postService.findById(postId);
        Page<PostReaction> reactions = postReactionService
                .findAllByOwnerAndReactionType(post, type, Integer.parseInt(offset), Integer.parseInt(limit));
        List<PostReactionResponse> items = reactions
                .map(postMapper::toPostReactionResponse)
                .toList();

        return ResponseEntity.status(OK).body(PaginateResponse.<PostReactionResponse>builder()
                .items(items)
                .pagination(new Pagination(Integer.parseInt(offset), Integer.parseInt(limit), reactions.getTotalElements()))
                .build()
        );
    }

    @Operation(summary = "Get post media reactions", description = "Get reactions to of post media")
    @GetMapping("/{mediaId}/media-reactions")
    @ResponseStatus(OK)
    ResponseEntity<PaginateResponse<PostReactionResponse>> getPostMediaReactions(@PathVariable String mediaId,
                                                                            @RequestParam(required = false) ReactionType type,
                                                                            @RequestParam(defaultValue = "0") String offset,
                                                                            @RequestParam(defaultValue = "100") String limit) {
        PostMedia media = fileService.findById(mediaId);
        Page<PostMediaReaction> reactions = postMediaReactionService
                .findAllByOwnerAndReactionType(media, type, Integer.parseInt(offset), Integer.parseInt(limit));
        List<PostReactionResponse> items = reactions
                .map(postMapper::toPostReactionResponse)
                .toList();

        return ResponseEntity.status(OK).body(PaginateResponse.<PostReactionResponse>builder()
                .items(items)
                .pagination(new Pagination(Integer.parseInt(offset), Integer.parseInt(limit), reactions.getTotalElements()))
                .build()
        );
    }

    /*_____________________________________________________CREATE-REACTION__________________________________________________________*/
    @Operation(summary = "Reaction to a post", description = "Reaction to a post with a reaction type")
    @PostMapping("/{postId}/reactions")
    @ResponseStatus(CREATED)
    ResponseEntity<PostReactionResponse> reactionPost(@PathVariable String postId, @RequestParam ReactionType type) {
        SecurityContext context = SecurityContextHolder.getContext();
        User current = userService.findByEmail(context.getAuthentication().getName());
        Post post = postService.findById(postId);
        PostReaction reaction = postReactionService.react(post, current, type);

        return ResponseEntity.status(CREATED).body(postMapper.toPostReactionResponse(reaction));
    }

    @Operation(summary = "Reaction to a post media", description = "Reaction to a post media with a reaction type")
    @PostMapping("/{mediaId}/media-reactions")
    @ResponseStatus(CREATED)
    ResponseEntity<PostReactionResponse> reactionPostMedia(@PathVariable String mediaId, @RequestParam ReactionType type) {
        SecurityContext context = SecurityContextHolder.getContext();
        User current = userService.findByEmail(context.getAuthentication().getName());
        PostMedia media = fileService.findById(mediaId);
        PostMediaReaction reaction = postMediaReactionService.react(media, current, type);

        return ResponseEntity.status(CREATED).body(postMapper.toPostReactionResponse(reaction));
    }

    /*_____________________________________________________DELETE-REACTION__________________________________________________________*/
    @Operation(summary = "Delete post reaction", description = "Delete a reaction to a post")
    @DeleteMapping("/{postId}/reactions")
    @ResponseStatus(OK)
    ResponseEntity<PostReactionResponse> deletePostReaction(@PathVariable String postId) {
        SecurityContext context = SecurityContextHolder.getContext();
        User current = userService.findByEmail(context.getAuthentication().getName());
        Post post = postService.findById(postId);
        PostReaction reaction = postReactionService.findByOwnerAndUser(post, current);
        postReactionService.deleteReaction(reaction);

        return ResponseEntity.status(OK).body(postMapper.toPostReactionResponse(reaction));
    }

    @Operation(summary = "Delete post media reaction", description = "Delete a reaction to a post media")
    @DeleteMapping("/{mediaId}/media-reactions")
    @ResponseStatus(OK)
    ResponseEntity<PostReactionResponse> deletePostMediaReaction(@PathVariable String mediaId) {
        SecurityContext context = SecurityContextHolder.getContext();
        User current = userService.findByEmail(context.getAuthentication().getName());
        PostMedia media = fileService.findById(mediaId);
        PostMediaReaction reaction = postMediaReactionService.findByOwnerAndUser(media, current);
        postMediaReactionService.deleteReaction(reaction);

        return ResponseEntity.status(OK).body(postMapper.toPostReactionResponse(reaction));
    }

    private List<PostMedia> getPostMedias(Post post) {
        return postService.previewMedias(post);
    }

}
