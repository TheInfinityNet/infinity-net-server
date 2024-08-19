package com.infinitynet.server.repositories;

import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.PostReaction;
import com.infinitynet.server.enums.ReactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, PostReaction.PostReactionId> {

    Page<PostReaction> findAllByPostAndReactionType(Post post, ReactionType type, Pageable pageable);

    Page<PostReaction> findAllByPost(Post post, Pageable pageable);

    Long countByPostAndReactionType(Post post, ReactionType type);

    Long countByPost(Post post);

}
