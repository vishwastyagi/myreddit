package com.mycompany.redditclone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Lazy;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

import com.mycompany.redditclone.model.User;

import java.time.Instant;

@Data //Generate getters and setters and other do other work
@Entity
@Builder // Generate builder method of our class. uses builder design pattern. to create object of our class
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @NotBlank(message = "Post name cannot be empty or null")
    private String postName;

    @Nullable
    private String url;

    @Nullable
    @Lob // Because we can have large chunk of text stored in this field
    private String description;

    private Integer voteCount=0;

    // Reference to user who will do post.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId", referencedColumnName = "userId")
    private User user;

    // Storing time on which post is created
    private Instant createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="subredditId",referencedColumnName = "subredditId")
    private Subreddit subreddit;
}
