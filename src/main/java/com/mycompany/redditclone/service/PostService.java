package com.mycompany.redditclone.service;

import com.mycompany.redditclone.dto.PostRequest;
import com.mycompany.redditclone.dto.PostResponse;
import com.mycompany.redditclone.exception.PostNotFoundException;
import com.mycompany.redditclone.exception.SpringRedditException;
import com.mycompany.redditclone.exception.SubredditNotFoundException;
import com.mycompany.redditclone.mapper.PostMapper;
import com.mycompany.redditclone.model.Post;
import com.mycompany.redditclone.model.Subreddit;
import com.mycompany.redditclone.model.User;
import com.mycompany.redditclone.repository.PostRepository;
import com.mycompany.redditclone.repository.SubredditRepository;
import com.mycompany.redditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PostMapper postMapper;

    public PostRequest save(PostRequest postRequest) {
        // Get subreddit by name
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SubredditNotFoundException("Subreddit not found for name: " + postRequest.getSubredditName()));
        // Find details of user who made the request
        User user = authService.getCurrentUser();
        Post post = postMapper.map(postRequest, subreddit, user);
        Post save=postRepository.save(post);
        postRequest.setPostId(save.getPostId());
        return postRequest;
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id.toString()));
        return postMapper.mapToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SubredditNotFoundException(subredditId.toString()));

        List<Post> posts = postRepository.findAllBySubreddit(subreddit);

        return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return postRepository.findByUser(user)
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
