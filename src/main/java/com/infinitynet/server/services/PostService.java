package com.infinitynet.server.services;

import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.PostReaction;
import com.infinitynet.server.entities.User;
import com.infinitynet.server.enums.PostVisibility;
import com.infinitynet.server.enums.ReactionType;
import org.springframework.stereotype.Service;

@Service
public interface PostService {

    Post findById(String id);

    Post createPost(User owner, String content, PostVisibility visibility);

    PostReaction createReactionPost(User current, Post post, ReactionType reactionType);

}
