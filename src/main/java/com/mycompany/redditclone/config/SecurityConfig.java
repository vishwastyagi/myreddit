package com.mycompany.redditclone.config;

import com.mycompany.redditclone.security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity  // This is main annotation, enables the web security module in our project
@AllArgsConstructor
// Holds complete security configuration of backend.
// WebSecurityConfigurerAdapter is base base for our SecurityConfig class.
// It provides all the default security configuration, which we can override and customize in our application
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // UserDetailsService loads the user detail from different sources, in our case it would be database and
    // provides user data to spring. This an interface we have to create implementing class
    private final UserDetailsService userDetailsService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

   @Override
   public void configure(HttpSecurity httpSecurity) throws Exception {
       // Disable csrf protection for backend. We disable it because csrf attacks mainly occurs when there are session
       // and when we are using cookies to authenticate the session information, as rest api are stateless and we are using
       // json web token for authorization, we can safely disable csrf.
       // we all incoming request coming to backend api whose endpoint url starts with /api/auth/
       //Any other request which does not this pattern(/api/auth/) should be authenticated
       httpSecurity.csrf().disable().authorizeRequests()
               .antMatchers("/api/auth/**")
               .permitAll()
               //We are excluding these points from spring security configuration. Visitors can see the posts, subreddit
               .antMatchers(HttpMethod.GET,"/api/subreddit")
               .permitAll()
               .antMatchers(HttpMethod.GET,"/api/posts/")
               .permitAll()
               .antMatchers(HttpMethod.GET,"/api/posts/**")
               .permitAll()
               // without below mapping we will get 403 error while accessing the swagger documentation
               // this will allow swagger request without token
               .antMatchers("/v2/api-docs",
                       "/configuration/ui",
                       "/swagger-resources/**",
                       "/configuration/security",
                       "/swagger-ui.html",
                       "/webjars/**")
               .permitAll()
               // end of swagger mappings
               .anyRequest()
               .authenticated();

       // We have to make that spring security knows about JwtAuthenticationFilter class.
       // Spring first check access jwt token, before try UsernamePasswordAuthentication
       httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
   }

    // Injecting AuthenticationManagerBuilder,
   // AuthenticationManagerBuilder is used to create authentication manager
    @Autowired
   public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
       // userDetailsService() takes input of type UserDetailService
       authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
   }

   @Bean
   //  PasswordEncoder is an interface
    PasswordEncoder passwordEncoder(){
       // BCryptPasswordEncoder class implements BCrypt hashing algo
       return new BCryptPasswordEncoder();
   }
}
