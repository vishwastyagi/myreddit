package com.mycompany.redditclone.service;

import com.mycompany.redditclone.dto.SubredditDto;
import com.mycompany.redditclone.exception.SpringRedditException;
import com.mycompany.redditclone.mapper.SubredditMapper;
import com.mycompany.redditclone.model.Subreddit;
import com.mycompany.redditclone.model.User;
import com.mycompany.redditclone.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;
    private final AuthService authService;


    @Transactional
    public SubredditDto save(SubredditDto subredditDto){
       // Subreddit subreddit= mapSubredditDto(subredditDto);
        User user = authService.getCurrentUser();
        Subreddit subreddit= subredditMapper.mapDtoToSubreddit(subredditDto,user);
        Subreddit save=subredditRepository.save(subreddit);
        subredditDto.setId(save.getSubredditId());
        return subredditDto;
    }

    @Transactional(readOnly=true)
    public List<SubredditDto> getAll(){
       // return subredditRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
        return subredditRepository.findAll().stream().map(subredditMapper::mapSubredditToDto).collect(Collectors.toList());
    }

    public SubredditDto getSubreddit(Long id) {
       Subreddit subreddit =  subredditRepository.findById(id).orElseThrow(()->new SpringRedditException("No Subreddit found for the id: "+id));
       return  subredditMapper.mapSubredditToDto(subreddit);
    }

   /* private SubredditDto mapToDto(Subreddit subreddit) {
        return SubredditDto.builder().name(subreddit.getName())
                .description(subreddit.getDescription())
                .id(subreddit.getId()).numberOfPosts(subreddit.getPosts().size()).build();
    }*/

     /*
    // Uses builder pattern from Subreddit
    private Subreddit mapSubredditDto(SubredditDto subredditDto) {
        return Subreddit.builder().name(subredditDto.getName())
                            .description(subredditDto.getDescription())
                            .build();
    }*/
}
