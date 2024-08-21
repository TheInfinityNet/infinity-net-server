package com.infinitynet.server.services.impls;

import com.infinitynet.server.entities.*;
import com.infinitynet.server.enums.PostType;
import com.infinitynet.server.enums.PostVisibility;
import com.infinitynet.server.enums.ReactionType;
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
    public PostReaction findByPostAndUser(Post post, User user) {
        return postReactionRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new PostException(POST_REACTION_NOT_FOUND, NOT_FOUND));
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
