package com.infinitynet.server.services.impls;

import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.PostReaction;
import com.infinitynet.server.entities.User;
import com.infinitynet.server.enums.PostVisibility;
import com.infinitynet.server.enums.ReactionType;
import com.infinitynet.server.exceptions.post.PostErrorCode;
import com.infinitynet.server.exceptions.post.PostException;
import com.infinitynet.server.repositories.PostReactionRepository;
import com.infinitynet.server.repositories.PostRepository;
import com.infinitynet.server.services.PostService;
import jakarta.persistence.EntityManager;
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
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static com.infinitynet.server.exceptions.post.PostErrorCode.POST_NOT_FOUND;
import static com.infinitynet.server.exceptions.post.PostErrorCode.POST_REACTION_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostServiceImpl implements PostService {

    PostRepository postRepository;

    PostReactionRepository postReactionRepository;

    @Override
    public Post findById(String id) {
        return postRepository.findById(id).orElseThrow(() -> new PostException(POST_NOT_FOUND, NOT_FOUND));
    }

    @Override
    public Long countByPostAndReactionType(Post post, ReactionType type) {
        return (type != null)
                ? postReactionRepository.countByPostAndReactionType(post, type)
                : postReactionRepository.countByPost(post);
    }

    @Override
    @Transactional
    public Post createPost(User owner, String content, PostVisibility visibility) {
        return postRepository.save(Post.builder()
                .user(owner)
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
    public PostReaction findById(PostReaction.PostReactionId id) {
        return postReactionRepository.findById(id)
                .orElseThrow(() -> new PostException(POST_REACTION_NOT_FOUND, NOT_FOUND));
    }

    @Override
    @Transactional
    public PostReaction createReactionPost(User current, Post post, ReactionType reactionType) {
        if (post.getPostVisibility().equals(PostVisibility.ONLY_ME)) {
            if (!current.getId().equals(post.getUser().getId()))
                throw new PostException(PostErrorCode.POST_NOT_FOUND, NOT_FOUND);
        }
        return postReactionRepository.save(PostReaction.builder()
                .id(new PostReaction.PostReactionId(current.getId(), post.getId()))
                .user(current)
                .post(post)
                .reactionType(reactionType)
                .build());
    }

    @Override
    @Transactional
    public void deleteReactionPost(PostReaction postReaction) {
        postReactionRepository.deleteById(postReaction.getId());
    }

}
