package com.mycompany.redditclone.repository;


import com.mycompany.redditclone.model.Post;
import com.mycompany.redditclone.model.User;
import com.mycompany.redditclone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    // We first find user by post and user information, then order the results by vote id in desc order and get the top result.
    // Getting the recent vote by the user
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
