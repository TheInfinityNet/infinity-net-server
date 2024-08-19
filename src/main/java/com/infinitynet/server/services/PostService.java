package com.infinitynet.server.services;

import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.PostReaction;
import com.infinitynet.server.entities.User;
import com.infinitynet.server.enums.PostVisibility;
import com.infinitynet.server.enums.ReactionType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface PostService {

    Post findById(String id);

    Long countByPostAndReactionType(Post post, ReactionType type);

    Post createPost(User owner, String content, PostVisibility visibility);

    Page<PostReaction> findAllByPostAndReactionType(Post post, ReactionType type, int offset, int limit);

    PostReaction findById(PostReaction.PostReactionId id);

    PostReaction createReactionPost(User current, Post post, ReactionType reactionType);

    void deleteReactionPost(PostReaction postReaction);

}
