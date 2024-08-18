package com.infinitynet.server.repositories;

import com.infinitynet.server.entities.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, String> {

}
