package com.mycompany.redditclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket redditApi(){
        //Docket is Springfox’s, primary api configuration mechanism is initialized for swagger specification 2.0
        // Docket is summary or the other  brief statement  of the contents of a document; an abstract
        // Congigure it to show what comes out to the swagger documentation
        // We used everything default except ApiInfo
        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("Reddit API")
                .version("1.0")
                .description("API for Reddit Application")
                .contact(new Contact("Vishwas","http://www.google.com","hearvishwas@gmail.com"))
                .license("Apache license version 2.0")
                .build();
    }

}
