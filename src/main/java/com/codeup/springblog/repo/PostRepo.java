package com.codeup.springblog.repo;

import com.codeup.springblog.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepo extends JpaRepository<Post, Long> {
    List<Post> findByTitle(String titleToSearchFor);


    @Query("from Post post where post.body like %:term%")
    List<Post> searchByBodyLike(@Param("term") String term);
}
