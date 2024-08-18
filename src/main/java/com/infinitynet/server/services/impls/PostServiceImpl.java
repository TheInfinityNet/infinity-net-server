package com.infinitynet.server.services.impls;

import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.User;
import com.infinitynet.server.enums.PostVisibility;
import com.infinitynet.server.repositories.PostRepository;
import com.infinitynet.server.services.PostService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostServiceImpl implements PostService {

    PostRepository postRepository;

    @Override
    @Transactional
    public Post createPost(User owner, String content, PostVisibility visibility) {
        return postRepository.save(Post.builder()
                .user(owner)
                .postVisibility(visibility)
                .content(content)
                .build());
    }

}
