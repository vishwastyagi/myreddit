package com.mycompany.redditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubredditDto {
    // Id of subreddit
    private Long id;
    private String name;
    private String description;
    // Number of post inside subreddit
    private Integer numberOfPosts;
}
