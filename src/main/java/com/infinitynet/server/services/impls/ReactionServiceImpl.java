package com.infinitynet.server.services.impls;

import com.infinitynet.server.entities.*;
import com.infinitynet.server.enums.PrivacySetting;
import com.infinitynet.server.enums.ReactionType;
import com.infinitynet.server.exceptions.post.PostErrorCode;
import com.infinitynet.server.exceptions.post.PostException;
import com.infinitynet.server.repositories.PostMediaReactionRepository;
import com.infinitynet.server.repositories.PostReactionRepository;
import com.infinitynet.server.services.ReactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static com.infinitynet.server.exceptions.post.PostErrorCode.POST_REACTION_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReactionServiceImpl<O, R> implements ReactionService<O, R> {

    PostReactionRepository postReactionRepository;

    PostMediaReactionRepository postMediaReactionRepository;

    @Override
    public Long countByOwnerAndReactionType(O owner, ReactionType reactionType) {
        return switch (owner.getClass().getSimpleName()) {
            case "Post" -> (reactionType != null)
                    ? postReactionRepository.countByPostAndReactionType((Post) owner, reactionType)
                    : postReactionRepository.countByPost((Post) owner);

            case "PostMedia" -> (reactionType != null)
                        ? postMediaReactionRepository.countByPostMediaAndReactionType((PostMedia) owner, reactionType)
                        : postMediaReactionRepository.countByPostMedia((PostMedia) owner);

            default -> throw new PostException(POST_REACTION_NOT_FOUND, UNPROCESSABLE_ENTITY);
        };
    }

    @Override
    public Page<R> findAllByOwnerAndReactionType(O owner, ReactionType reactionType, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        return switch (owner.getClass().getSimpleName()) {
            case "Post" -> (reactionType != null)
                    ? (Page<R>) postReactionRepository.findAllByPostAndReactionType((Post) owner, reactionType, pageable)
                    : (Page<R>) postReactionRepository.findAllByPost((Post) owner, pageable);

            case "PostMedia" -> (reactionType != null)
                        ? (Page<R>) postMediaReactionRepository.findAllByPostMediaAndReactionType((PostMedia) owner, reactionType, pageable)
                        : (Page<R>) postMediaReactionRepository.findAllByPostMedia((PostMedia) owner, pageable);

            default -> throw new PostException(POST_REACTION_NOT_FOUND, UNPROCESSABLE_ENTITY);
        };
    }

    @Override
    public R findByOwnerAndUser(O owner, User user) {
        return switch (owner.getClass().getSimpleName()) {
            case "Post" -> (R) postReactionRepository.findByPostAndUser((Post) owner, user)
                    .orElseThrow(() -> new PostException(POST_REACTION_NOT_FOUND, NOT_FOUND));

            case "PostMedia" -> (R) postMediaReactionRepository.findByPostMediaAndUser((PostMedia) owner, user)
                    .orElseThrow(() -> new PostException(POST_REACTION_NOT_FOUND, NOT_FOUND));

            default -> throw new PostException(POST_REACTION_NOT_FOUND, UNPROCESSABLE_ENTITY);
        };
    }

    @Override
    public R getCurrentUserReaction(O owner, User user) {
        try {
            return findByOwnerAndUser(owner, user);

        } catch (PostException e) {
            return null;
        }
    }

    @Override
    public R react(O owner, User user, ReactionType reactionType) {
        PrivacySetting privacySetting = switch (owner.getClass().getSimpleName()) {
            case "Post" -> ((Post) owner).getPrivacySetting();

            case "PostMedia" -> ((PostMedia) owner).getPost().getPrivacySetting();

            default -> throw new PostException(POST_REACTION_NOT_FOUND, UNPROCESSABLE_ENTITY);
        };

        User ownerUser = switch (owner.getClass().getSimpleName()) {
            case "Post" -> ((Post) owner).getUser();

            case "PostMedia" -> ((PostMedia) owner).getPost().getUser();

            default -> throw new PostException(POST_REACTION_NOT_FOUND, UNPROCESSABLE_ENTITY);
        };

        if (privacySetting.equals(PrivacySetting.ONLY_ME)) {
            if (!user.getId().equals(ownerUser.getId()))
                throw new PostException(PostErrorCode.POST_NOT_FOUND, NOT_FOUND);
        }

        return switch (owner.getClass().getSimpleName()) {
            case "Post" -> (R) postReactionRepository.save(PostReaction.builder()
                    .post((Post) owner)
                    .user(user)
                    .reactionType(reactionType)
                    .build());

            case "PostMedia" -> (R) postMediaReactionRepository.save(PostMediaReaction.builder()
                    .postMedia((PostMedia) owner)
                    .user(user)
                    .reactionType(reactionType)
                    .build());

            default -> throw new PostException(POST_REACTION_NOT_FOUND, UNPROCESSABLE_ENTITY);
        };
    }

    @Override
    public void deleteReaction(R reaction) {
        switch (reaction.getClass().getSimpleName()) {
            case "PostReaction" -> postReactionRepository.delete((PostReaction) reaction);

            case "PostMediaReaction" -> postMediaReactionRepository.delete((PostMediaReaction) reaction);

            default -> throw new PostException(POST_REACTION_NOT_FOUND, UNPROCESSABLE_ENTITY);
        }
    }
}
