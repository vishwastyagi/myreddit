package com.mycompany.redditclone.repository;

import com.mycompany.redditclone.model.Post;

import com.mycompany.redditclone.model.Subreddit;
import com.mycompany.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findByUser(User user);
}
