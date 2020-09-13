package com.mycompany.redditclone.exception;

public class SpringRedditException extends RuntimeException {
    public SpringRedditException(String s) {
        super(s);
    }
    public SpringRedditException(String s, Exception e) {
        super(s+e.getMessage());
    }
}
