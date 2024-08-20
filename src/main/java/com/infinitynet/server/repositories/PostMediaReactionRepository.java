package com.infinitynet.server.repositories;

import com.infinitynet.server.entities.PostMedia;
import com.infinitynet.server.entities.PostMediaReaction;
import com.infinitynet.server.enums.ReactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostMediaReactionRepository extends JpaRepository<PostMediaReaction, PostMediaReaction.PostMediaReactionId> {

    Page<PostMediaReaction> findAllByPostMediaAndReactionType(PostMedia media, ReactionType type, Pageable pageable);

    Page<PostMediaReaction> findAllByPostMedia(PostMedia media, Pageable pageable);

    Long countByPostMediaAndReactionType(PostMedia media, ReactionType type);

    Long countByPostMedia(PostMedia media);

}
