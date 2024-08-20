package com.infinitynet.server.repositories;

import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {

    Page<Post> findAllByUser(User owner, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.postVisibility = 'PUBLIC' " +
            "AND p.postType = 'USER_POST' OR p.postType = 'SHARED_POST' ORDER BY p.createdAt DESC")
    Page<Post> findAll(Pageable pageable);
}
