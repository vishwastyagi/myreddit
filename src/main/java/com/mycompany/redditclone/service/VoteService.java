package com.mycompany.redditclone.service;

import com.mycompany.redditclone.dto.VoteDto;
import com.mycompany.redditclone.exception.PostNotFoundException;
import com.mycompany.redditclone.exception.SpringRedditException;
import com.mycompany.redditclone.model.Post;
import com.mycompany.redditclone.model.Vote;
import com.mycompany.redditclone.model.VoteType;
import com.mycompany.redditclone.repository.PostRepository;
import com.mycompany.redditclone.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto) {
        // Fetch post which has to be updated by vote
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post Not Found with ID - " + voteDto.getPostId()));

        // Find recent vote, which was submitted by this user for the post
        // We are using spring data jpa
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());

        // User should do upvote or downvote only once. User can change the vote type.
        // We should not allow to vote in same direction twice
        if (voteByPostAndUser.isPresent() &&
                voteByPostAndUser.get().getVoteType()
                        .equals(voteDto.getVoteType())) {
            throw new SpringRedditException("You have already "
                    + voteDto.getVoteType() + "'d for this post");
        }
        // We either add or subtract vote count by 1, based on vote type
        if (VoteType.UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }

        voteRepository.save(mapToVote(voteDto, post));
        postRepository.save(post);
    }

    // We are manually mapping the fields from DTO to domain class.
    // We have not created mapper class.
    // Used builder method, Vote class has @Builder
    private Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}