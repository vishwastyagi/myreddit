package com.mycompany.redditclone;

import com.mycompany.redditclone.config.SwaggerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // To enable asynchronous processing in our application( like sending mail)
@Import(SwaggerConfiguration.class) // At application startup spring will invoke spring fox and spring fox is going
                                    // to scan all the rest controller coponents in our application and corresponding dtos
                                    // for this rest controller and generate the rest api documentation
public class RedditcloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedditcloneApplication.class, args);
    }

}
