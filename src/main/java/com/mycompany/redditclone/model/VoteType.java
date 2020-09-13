package com.mycompany.redditclone.model;
import com.mycompany.redditclone.exception.SpringRedditException;

import java.util.Arrays;
public enum VoteType {
    UPVOTE(1), DOWNVOTE(-1),
    ;

    // For upvote direct is 1, for downvote direction is -1
    private int direction;

    VoteType(int direction) {
    }

    public static VoteType lookup(Integer direction) {
        return Arrays.stream(VoteType.values())
                .filter(value -> value.getDirection().equals(direction))
                .findAny()
                .orElseThrow(() -> new SpringRedditException("Vote not found"));
    }

    public Integer getDirection() {
        return direction;
    }
}