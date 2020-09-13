package com.mycompany.redditclone.mapper;

import com.mycompany.redditclone.dto.SubredditDto;
import com.mycompany.redditclone.model.Post;
import com.mycompany.redditclone.model.Subreddit;
import com.mycompany.redditclone.model.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface SubredditMapper {

    // Mapstruct will identify and generate mapping logic automatically for matching fields(same in Subreddit and SubredditDto)
    // For the remaining fields like numberOfPosts(of type integer), we have to create mapping from List<Post>(in Subreddit) to integer numberOfPosts
    // that is we have to return size of this list. To do that we create a method "mapPosts" and
    // We can tell mapstruct to use "mapPosts" method when mapping numberOfPosts field by adding
    // the @Mapping annotation and target as field name, and expression to call actual method
    // ctrl+shift+f9 to compile and see the constrcted code by mapstruct
    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
    SubredditDto mapSubredditToDto(Subreddit subreddit);

    default Integer mapPosts(List<Post> numberOfPosts){
        return numberOfPosts.size();
    }

    // Mapping from SubredditDto to Subreddit entity. Ignore "posts" field
    @InheritInverseConfiguration
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    Subreddit mapDtoToSubreddit(SubredditDto subredditDto, User user);
}
