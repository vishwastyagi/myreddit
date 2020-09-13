package com.mycompany.redditclone.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    // We will intercept the request and fetch the token from the request headers, bcz client sends the token to server as part of request headers
    // by following the bearers keys
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);

        // Now if we got valid token we have to load user from the database and set userdetails in spring security context
        if(StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {

            // First we have to get the username from the token, we are using username as subject when creating token
            String username = jwtProvider.getUsernameFromJwt(jwt);

            // Get the user from the db and set the user details into the security context
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // We create object of UsernamePasswordAuthenticationToken and store it inside spring  SecurityContext by SecurityContextHolder
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // If token is valid spring is going to find user details inside spring security context and it will fulfill
        // our request and if not it will throw the exception
        filterChain.doFilter(request,response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        // Structure of token will be: Bearer <JWT>
        String bearerToken = request.getHeader("Authorization");
        // We have exclude the term Bearer and extract token
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;

    }

}
