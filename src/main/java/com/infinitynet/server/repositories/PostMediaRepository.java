package com.infinitynet.server.repositories;

import com.infinitynet.server.entities.FileMetadata;
import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, String> {

    List<PostMedia> findAllByPostOrderByCreatedAtDesc(Post post);

    @Query("SELECT pm FROM PostMedia pm WHERE pm.post = :post ORDER BY pm.createdAt DESC LIMIT 3 OFFSET 0")
    List<PostMedia> previewMedias(Post post);

}
