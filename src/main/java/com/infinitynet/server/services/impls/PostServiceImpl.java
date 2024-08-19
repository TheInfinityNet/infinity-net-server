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
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.infinitynet.server.exceptions.post.PostErrorCode.POST_NOT_FOUND;
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
    @Transactional
    public Post createPost(User owner, String content, PostVisibility visibility) {
        return postRepository.save(Post.builder()
                .user(owner)
                .postVisibility(visibility)
                .content(content)
                .build());
    }

    @Override
    @Transactional
    public PostReaction createReactionPost(User current, Post post, ReactionType reactionType) {
        return postReactionRepository.save(PostReaction.builder()
                .id(new PostReaction.PostReactionId(current.getId(), post.getId()))
                .user(current)
                .post(post)
                .reactionType(reactionType)
                .build());
    }

}
