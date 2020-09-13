package com.mycompany.redditclone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
// Backend block the request from angular UI, if we do not specify CORS policy
public class WebConfig implements WebMvcConfigurer {

    @Override
    // This method has all CORS related configuration which we require for the backend
    // We are accepting request from all origins to our backend. In prod this is not the right way to do
    public void addCorsMappings(CorsRegistry corsRegistry){
        corsRegistry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("*")
                    .maxAge(3600L)
                    .allowedHeaders("*")
                    .exposedHeaders("Authorization")
                    .allowCredentials(true);
    }

    // Once we enable web mvc with @EnableWebMvc for CORS configuration, our swapper rest call will not work. Gives 404,
    // bcz, spring mvc does not know how to handle the web jars which is coming as part swagger spring fox library.
    // To handle such situation, we add below method which tells spring mvc where to look for reswagger-ui.html and webjars
    // We pointing to location of webjars and swagger-ui.html can be found in the classpath.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META_INF/resources");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
