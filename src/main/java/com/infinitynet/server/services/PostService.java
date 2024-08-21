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

    Post createPost(User owner, String content, PostType type, PostVisibility visibility);

    PostReaction findByPostAndUser(Post post, User user);

    List<PostMedia> previewMedias(Post post);

    Comment createComment(User user, Post post, String content);

    Comment replyComment(User user, Comment parentComment, String content);


}
