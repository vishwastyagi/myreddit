package com.mycompany.redditclone.service;

import com.mycompany.redditclone.dto.AutheticationResponse;
import com.mycompany.redditclone.dto.LoginRequest;
import com.mycompany.redditclone.dto.RefreshTokenRequest;
import com.mycompany.redditclone.dto.RegisterRequest;
import com.mycompany.redditclone.exception.SpringRedditException;
import com.mycompany.redditclone.model.NotificationEmail;
import com.mycompany.redditclone.model.User;
import com.mycompany.redditclone.model.VerificationToken;
import com.mycompany.redditclone.repository.UserRepository;
import com.mycompany.redditclone.repository.VerificationTokenRepository;
import com.mycompany.redditclone.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor

// Contains logic to register user(creating User object, storing to DB and sending out activation mail etc.)
// Contains logic to create username, password, authentication token and use authentication manager to perform login
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    // Autowire AuthenticationManager. This is an interface. It has many implementations. We have to create a bean in
    // SecurtiyConfig which extends the WebSecurityConfigurerAdapter. So whenever we wire authentication manager, spring
    // finds this WebSecurityConfigurerAdapter bean and inject into our class.
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    private final RefreshTokenService refreshTokenService;

    /* Using constructor injection is better than field injection
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    */

    @Transactional // We are interacting with RDBMS
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        // Java 8 class to get current time and date
        user.setCreatedDate(Instant.now());
        user.setEnabled(false);
        userRepository.save(user);

        // Generate token and send verification email
        String token = generateVerificationToken(user);
        // the url message redirect user to our server. Whenever user click on this url we take the token from url,
        // look token in our db, fetch the user and enable the user
        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), "Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
    }

    @Transactional
    // Here we have to query the token verification repository by token which we received as input
    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);
        // orElseThrow takes supplier as argument
        verificationTokenOptional.orElseThrow(() -> new SpringRedditException("Invalid Token"));
        VerificationToken verificationToken = verificationTokenOptional.get();
        Instant expiryDate = verificationToken.getExpiryDate();
        Instant currentTime=Instant.now();
        long duration = ChronoUnit.HOURS.between(expiryDate,currentTime);
        if(duration>10){
          throw new SpringRedditException("Token Link Expired.");
        }
        fetchUserAndEnable(verificationToken);
    }

    // Contains logic to authenticate the user.
    public AutheticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        // Store Authentication object inside the SecurityContext. If we have to check if user is logged in or not, we can to just
        // lookup the security context for the Authentication object and if we find the object then we can be sure that user is logged in
        // else user is not logged in
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        String token = jwtProvider.generateToken(authenticate);
        // To send this token back to user, we will use dto called a AutheticationResponse

        /*
            AutheticationResponse autheticationResponse = new AutheticationResponse();
            autheticationResponse.setAuthenticationToken(token);
            autheticationResponse.setUsername(loginRequest.getUsername());
            return autheticationResponse;
        */
        return AutheticationResponse.builder()
                .authenticationToken(token)
                // For now we are passing refresh token as empty string
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();

    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }

    // We should not use @Transactional on private method
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String userName = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new SpringRedditException("User not found with user name: " + userName));
        user.setEnabled(true);
        userRepository.save(user);
    }

    private String generateVerificationToken(User user) {
        // Generate unqiue random 128 bit value, which we can use as verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(Instant.now().plus(10, ChronoUnit.HOURS));

        verificationTokenRepository.save(verificationToken);
        return token;
    }


    public AutheticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        // throw runtime exception if token is not valid
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token=jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AutheticationResponse.builder()
                .authenticationToken(token)
                // For now we are passing refresh token as empty string
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }
}
