package com.infinitynet.server.services;

import com.infinitynet.server.entities.*;
import com.infinitynet.server.enums.PostType;
import com.infinitynet.server.enums.PostVisibility;
import com.infinitynet.server.enums.ReactionType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {

    Post findById(String id);

    Page<Post> findAllByUser(User user, int offset, int limit);

    Page<Post> findAll(int offset, int limit);

    Long countByPostAndReactionType(Post post, ReactionType type);

    Long countByPostMediaAndReactionType(PostMedia media, ReactionType type);

    Post createPost(User owner, String content, PostType type, PostVisibility visibility);

    Page<PostReaction> findAllByPostAndReactionType(Post post, ReactionType type, int offset, int limit);

    Page<PostMediaReaction> findAllByPostAndReactionType(PostMedia media, ReactionType type, int offset, int limit);

    PostReaction findByPostAndUser(Post post, User user);

    PostMediaReaction findByPostMediaAndUser(PostMedia media, User user);

    PostReaction reactionPost(User current, Post post, ReactionType reactionType);

    PostMediaReaction reactionPostMedia(User current, PostMedia media, ReactionType reactionType);

    void deletePostReaction(PostReaction reaction);

    void deleteMediaPostReaction(PostMediaReaction reaction);

    List<PostMedia> findAllByPost(Post post);

    List<PostMedia> previewMedias(Post post);

}
