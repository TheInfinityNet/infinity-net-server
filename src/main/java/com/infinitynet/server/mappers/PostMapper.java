package com.infinitynet.server.mappers;

import com.infinitynet.server.dtos.responses.post.PostReactionResponse;
import com.infinitynet.server.dtos.responses.post.PostResponse;
import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.PostReaction;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    PostResponse toPostResponse(Post post);
    @AfterMapping
    default void customizeDto(Post post, @MappingTarget PostResponse postResponse) {
        postResponse.setUserId(post.getUser().getId());
    }

    PostReactionResponse toPostReactionResponse(PostReaction reaction);
    @AfterMapping
    default void customizeDto(PostReaction reaction, @MappingTarget PostReactionResponse postReactionResponse) {
        postReactionResponse.setPostId(reaction.getPost().getId());
        postReactionResponse.setUserId(reaction.getUser().getId());
    }

}
