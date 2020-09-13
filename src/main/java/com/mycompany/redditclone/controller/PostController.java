package com.mycompany.redditclone.controller;

import com.mycompany.redditclone.dto.PostRequest;
import com.mycompany.redditclone.dto.PostResponse;
import com.mycompany.redditclone.dto.SubredditDto;
import com.mycompany.redditclone.model.Post;
import com.mycompany.redditclone.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    /*@PostMapping
    public ResponseEntity<Void> createPost(@RequestBody PostRequest postRequest){
        Post post=postService.save(postRequest);

        // By sending response entity, we can control what kind of response we are sending back to client
       return new ResponseEntity<>(HttpStatus.CREATED);

    }*/
    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody PostRequest postRequest){
        PostRequest saved=postService.save(postRequest);

        // By sending response entity, we can control what kind of response we are sending back to client
        return ResponseEntity.status(HttpStatus.CREATED).body("PostId: "+saved.getPostId());
    }
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return status(HttpStatus.OK).body(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return status(HttpStatus.OK).body(postService.getPost(id));
    }

    @GetMapping("by-subreddit/{id}")
    public ResponseEntity<List<PostResponse>> getPostsBySubreddit(@PathVariable Long id) {
        return status(HttpStatus.OK).body(postService.getPostsBySubreddit(id));
    }

    @GetMapping("by-user/{name}")
    public ResponseEntity<List<PostResponse>> getPostsByUsername(@PathVariable String username) {
        return status(HttpStatus.OK).body(postService.getPostsByUsername(username));
    }
}
