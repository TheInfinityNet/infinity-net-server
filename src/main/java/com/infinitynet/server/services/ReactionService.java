package com.infinitynet.server.services;

import com.infinitynet.server.entities.User;
import com.infinitynet.server.enums.ReactionType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ReactionService<O, R> {

    Long countByOwnerAndReactionType(O owner, ReactionType reactionType);

    Page<R> findAllByOwnerAndReactionType(O owner, ReactionType reactionType, int offset, int limit);

    R findByOwnerAndUser(O owner, User user);

    R getCurrentUserReaction(O owner, User user);

    R react(O owner, User user, ReactionType reactionType);

    void deleteReaction(R reaction);
}
