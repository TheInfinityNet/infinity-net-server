package com.infinitynet.server.services;

import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.User;
import com.infinitynet.server.enums.PostVisibility;
import org.springframework.stereotype.Service;

@Service
public interface PostService {

    Post createPost(User owner, String content, PostVisibility visibility);

}
