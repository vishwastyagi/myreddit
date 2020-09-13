package com.mycompany.redditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Long id;
    private String postName;
    private String url;
    private String description;
    private String userName;
    private String subredditName;
    private boolean upVote;
    private boolean downVote;
    // New fields
    // vote and comment related fields when fetching post
    private Integer voteCount;
    private Integer commentCount;
    // Duration of post creation. We will use library Timeago. Its is kotlin library
    private String duration;

}