package com.mycompany.redditclone.mapper;


import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.mycompany.redditclone.dto.PostRequest;
import com.mycompany.redditclone.dto.PostResponse;
import com.mycompany.redditclone.model.*;
import com.mycompany.redditclone.repository.CommentRepository;
import com.mycompany.redditclone.repository.VoteRepository;
import com.mycompany.redditclone.service.AuthService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Mapper(componentModel = "spring")
// We are using abstract class and not interface, bez we have added new fields in the dto and we need some
// dependencies to fill these details. For comment count we need access to commentRepository, vote count
// we need access to voteRepository, authservice is used to calculate vote count

// The good thing here is we updated mappings and we don't even touch PostService class. We updated methods in post mapper class
// We are able to decouple to mapping logic with actual logic(inside PostService)
public abstract class PostMapper {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthService authService;


    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    // postRequest object has description inside it
    @Mapping(target = "description", source = "postRequest.description")
    // Here source and target name fields are same,, below two mappings are optional here
    @Mapping(target = "subreddit", source = "subreddit")
    @Mapping(target = "user", source = "user")
    // default vote count is 0, bcz whenever we are saving post default vote count should be 0
    @Mapping(target = "voteCount", constant = "0")
    // create Post from PostRequest object. We will be passing parameters from PostService class
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "id", source = "postId")
    // Here source=subreddit.name means Post class has Subreddit class, Subreddit class has name
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
    // here source and target name fields are same, hence we can remove below 3v mappings
    @Mapping(target = "description", source = "description")
    @Mapping(target = "url", source = "url")
    @Mapping(target = "postName", source = "postName")
    // New mapping for new fields
    @Mapping(target = "commentCount", expression="java(commentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    public abstract PostResponse mapToDto(Post post);

    Integer commentCount(Post post) {
        return commentRepository.findByPost(post).size();
    }

    String getDuration(Post post) {
        // using(..) takes long as input, getCreatedDate() returns Instant
        // toEpochMilli() converts Instant to long, gives millisecond starting from 1st Jan, 1970 until now

        return TimeAgo.using(post.getCreatedDate().toEpochMilli());
    }
    boolean isPostUpVoted(Post post) {
        return checkVoteType(post, VoteType.UPVOTE);
    }

    boolean isPostDownVoted(Post post) {
        return checkVoteType(post, VoteType.DOWNVOTE);
    }

    // Find the latest vote submitted by the given user. We are doing this only if user is logged in else return false
    private boolean checkVoteType(Post post, VoteType voteType) {
        if (authService.isLoggedIn()) {
            Optional<Vote> voteForPostByUser =
                    voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,
                            authService.getCurrentUser());
            return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType))
                    .isPresent();
        }
        return false;
    }
}