package com.infinitynet.server.controllers;

import com.infinitynet.server.dtos.others.Pagination;
import com.infinitynet.server.dtos.requests.post.PostCreationRequest;
import com.infinitynet.server.dtos.responses.CommonResponse;
import com.infinitynet.server.dtos.responses.PaginateResponse;
import com.infinitynet.server.dtos.responses.post.PostMediaResponse;
import com.infinitynet.server.dtos.responses.post.PostReactionResponse;
import com.infinitynet.server.dtos.responses.post.PostResponse;
import com.infinitynet.server.entities.*;
import com.infinitynet.server.enums.ReactionType;
import com.infinitynet.server.exceptions.post.PostException;
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

    FileService<Post, PostMedia> fileService;

    UserService userService;

    PostMapper postMapper = PostMapper.INSTANCE;

    @Operation(summary = "Create a new post", description = "Create a new post with content and media files")
    @PostMapping
    @ResponseStatus(CREATED)
    ResponseEntity<CommonResponse<?>> createPost(@RequestPart @Valid PostCreationRequest request,
                                 @RequestPart(required = false) List<MultipartFile> mediaFiles) {
        SecurityContext context = SecurityContextHolder.getContext();
        User owner = userService.findByEmail(context.getAuthentication().getName());
        Post newPost = postService.createPost(owner, request.content(), null, request.visibility());

        if (mediaFiles != null && !mediaFiles.isEmpty()) fileService.uploadFiles(newPost, POST, mediaFiles);

        return ResponseEntity.status(CREATED).body(CommonResponse.builder()
                .message(getLocalizedMessage("create_post_success")).build());
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
                    PostReaction currentUsersReaction;
                    try {
                        currentUsersReaction = postService.findByPostAndUser(post, current);
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


    @Operation(summary = "Get post by id", description = "Get a post by id")
    @GetMapping("/{postId}")
    @ResponseStatus(OK)
    ResponseEntity<PostResponse> getPost(@PathVariable String postId) {
        SecurityContext context = SecurityContextHolder.getContext();
        User current = userService.findByEmail(context.getAuthentication().getName());
        Post post = postService.findById(postId);

        PostReaction currentUsersReaction;
        try {
            currentUsersReaction = postService.findByPostAndUser(post, current);
        } catch (PostException e) {
            currentUsersReaction = null;
        }

        List<PostMediaResponse> medias = postService.findAllByPost(post)
                .stream()
                .map(media -> {
                    PostMediaResponse dto = postMapper.toPostMediaResponse(media);
                    String url = fileService.getObjectUrl(media);
                    PostMediaReaction currentUsersMediaReaction;
                    try {
                        currentUsersMediaReaction = postService.findByPostMediaAndUser(media, current);
                    } catch (PostException e) {
                        currentUsersMediaReaction = null;
                    }
                    dto.setUrl(url);
                    dto.setCurrentUserReaction(postMapper.toPostReactionResponse(currentUsersMediaReaction));
                    dto.setReactionCounts(postService.countByPostMediaAndReactionType(media, null));
                    return dto;
                })
                .toList();

        PostResponse response = postMapper.toPostResponse(post);
        response.setReactionCounts(postService.countByPostAndReactionType(post, null));
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

    @Operation(summary = "Get post media reactions", description = "Get reactions to of post media")
    @GetMapping("/{mediaId}/media-reactions")
    @ResponseStatus(OK)
    ResponseEntity<PaginateResponse<PostReactionResponse>> getPostMediaReactions(@PathVariable String mediaId,
                                                                            @RequestParam(required = false) ReactionType type,
                                                                            @RequestParam(defaultValue = "0") String offset,
                                                                            @RequestParam(defaultValue = "100") String limit) {
        PostMedia media = fileService.findById(mediaId);
        Page<PostMediaReaction> reactions =
                postService.findAllByPostAndReactionType(media, type, Integer.parseInt(offset), Integer.parseInt(limit));
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
        PostReaction reaction = postService.reactionPost(current, post, type);

        return ResponseEntity.status(CREATED).body(postMapper.toPostReactionResponse(reaction));
    }

    @Operation(summary = "Reaction to a post media", description = "Reaction to a post media with a reaction type")
    @PostMapping("/{mediaId}/media-reactions")
    @ResponseStatus(CREATED)
    ResponseEntity<PostReactionResponse> reactionPostMedia(@PathVariable String mediaId, @RequestParam ReactionType type) {
        SecurityContext context = SecurityContextHolder.getContext();
        User current = userService.findByEmail(context.getAuthentication().getName());
        PostMedia media = fileService.findById(mediaId);
        PostMediaReaction reaction = postService.reactionPostMedia(current, media, type);

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
        PostReaction reaction = postService.findByPostAndUser(post, current);
        postService.deletePostReaction(reaction);

        return ResponseEntity.status(OK).body(postMapper.toPostReactionResponse(reaction));
    }

    @Operation(summary = "Delete post media reaction", description = "Delete a reaction to a post media")
    @DeleteMapping("/{mediaId}/media-reactions")
    @ResponseStatus(OK)
    ResponseEntity<PostReactionResponse> deletePostMediaReaction(@PathVariable String mediaId) {
        SecurityContext context = SecurityContextHolder.getContext();
        User current = userService.findByEmail(context.getAuthentication().getName());
        PostMedia media = fileService.findById(mediaId);
        PostMediaReaction reaction = postService.findByPostMediaAndUser(media, current);
        postService.deleteMediaPostReaction(reaction);

        return ResponseEntity.status(OK).body(postMapper.toPostReactionResponse(reaction));
    }

    /*__________________________________________COMMENT______________________________________________*/

}
