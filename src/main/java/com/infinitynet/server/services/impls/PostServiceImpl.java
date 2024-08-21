package com.infinitynet.server.services.impls;

import com.infinitynet.server.entities.*;
import com.infinitynet.server.enums.PostType;
import com.infinitynet.server.enums.PostVisibility;
import com.infinitynet.server.enums.ReactionType;
import com.infinitynet.server.exceptions.post.PostErrorCode;
import com.infinitynet.server.exceptions.post.PostException;
import com.infinitynet.server.repositories.*;
import com.infinitynet.server.services.PostService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.infinitynet.server.enums.PostType.USER_POST;
import static com.infinitynet.server.exceptions.post.PostErrorCode.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostServiceImpl implements PostService {

    PostRepository postRepository;

    PostReactionRepository postReactionRepository;

    PostMediaReactionRepository postMediaReactionRepository;

    PostMediaRepository postMediaRepository;

    CommentRepository commentRepository;

    @Override
    public Post findById(String id) {
        return postRepository.findById(id).orElseThrow(() -> new PostException(POST_NOT_FOUND, NOT_FOUND));
    }

    @Override
    public Page<Post> findAllByUser(User user, int offset, int limit) {
        return postRepository
                .findAllByUser(user, PageRequest.of(offset, limit, Sort.by("createdAt").descending()));
    }

    @Override
    public Page<Post> findAll(int offset, int limit) {
        return postRepository.findAll(PageRequest.of(offset, limit));
    }

    @Override
    public Long countByPostAndReactionType(Post post, ReactionType type) {
        return (type != null)
                ? postReactionRepository.countByPostAndReactionType(post, type)
                : postReactionRepository.countByPost(post);
    }

    @Override
    public Long countByPostMediaAndReactionType(PostMedia media, ReactionType type) {
        return (type != null)
                ? postMediaReactionRepository.countByPostMediaAndReactionType(media, type)
                : postMediaReactionRepository.countByPostMedia(media);
    }

    @Override
    @Transactional
    public Post createPost(User owner, String content, PostType type, PostVisibility visibility) {
        return postRepository.save(Post.builder()
                .user(owner)
                .postType((type == null) ? USER_POST : type)
                .postVisibility(visibility)
                .content(content)
                .build());
    }

    @Override
    public Page<PostReaction> findAllByPostAndReactionType(Post post, ReactionType type, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        return (type != null)
                ? postReactionRepository.findAllByPostAndReactionType(post, type, pageable)
                : postReactionRepository.findAllByPost(post, pageable);
    }

    @Override
    public Page<PostMediaReaction> findAllByPostAndReactionType(PostMedia media, ReactionType type, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        return (type != null)
                ? postMediaReactionRepository.findAllByPostMediaAndReactionType(media, type, pageable)
                : postMediaReactionRepository.findAllByPostMedia(media, pageable);
    }

    @Override
    public PostReaction findByPostAndUser(Post post, User user) {
        return postReactionRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new PostException(POST_REACTION_NOT_FOUND, NOT_FOUND));
    }

    @Override
    public PostMediaReaction findByPostMediaAndUser(PostMedia media, User user) {
        return postMediaReactionRepository.findByPostMediaAndUser(media, user)
                .orElseThrow(() -> new PostException(POST_REACTION_NOT_FOUND, NOT_FOUND));
    }

    @Override
    @Transactional
    public PostReaction reactionPost(User current, Post post, ReactionType reactionType) {
        if (post.getPostVisibility().equals(PostVisibility.ONLY_ME)) {
            if (!current.getId().equals(post.getUser().getId()))
                throw new PostException(PostErrorCode.POST_NOT_FOUND, NOT_FOUND);
        }
        return postReactionRepository.save(PostReaction.builder()
                .user(current)
                .post(post)
                .reactionType(reactionType)
                .build());
    }

    @Override
    public PostMediaReaction reactionPostMedia(User current, PostMedia media, ReactionType reactionType) {
        if (media.getPost().getPostVisibility().equals(PostVisibility.ONLY_ME)) {
            if (!current.getId().equals(media.getPost().getUser().getId()))
                throw new PostException(PostErrorCode.POST_NOT_FOUND, NOT_FOUND);
        }
        return postMediaReactionRepository.save(PostMediaReaction.builder()
                .user(current)
                .postMedia(media)
                .reactionType(reactionType)
                .build());
    }

    @Override
    @Transactional
    public void deletePostReaction(PostReaction reaction) {
        postReactionRepository.delete(reaction);
    }

    @Override
    public void deleteMediaPostReaction(PostMediaReaction reaction) {
        postMediaReactionRepository.delete(reaction);
    }

    @Override
    public List<PostMedia> findAllByPost(Post post) {
        return postMediaRepository.findAllByPostOrderByCreatedAtDesc(post);
    }

    @Override
    public List<PostMedia> previewMedias(Post post) {
        return postMediaRepository.previewMedias(post);
    }

    @Override
    public Comment createComment(User user, Post post, String content) {
        return null;
    }

    @Override
    public Comment replyComment(User user, Comment parentComment, String content) {
        Comment comment = Comment.builder()
                .user(user)
                .post(parentComment.getPost())
                .parentComment(parentComment)
                .build();

        Comment oldComment = commentRepository.findById("skljdfskjfh")
                .orElseThrow(() -> new PostException(COMMENT_NOT_FOUND, BAD_REQUEST));

        oldComment.setContent(content);

        commentRepository.save(oldComment);

        return null;
    }

}
