package com.mycompany.redditclone.security;

import com.mycompany.redditclone.exception.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.cert.CertificateException;


import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.security.*;
import java.time.Instant;
import java.util.Date;

// We are using JWT for authentication mechanism, the advantage is they are stateless and easy for server to identify whether a request is coming from valid user or not.
// But this behaviour make logout mechanism very tricky and bit difficult. There are 4 ways in to implement JWT invalidation mechanism
// 1. delete the token from client browser. This is must when application implement login functionality. But hacker may got access to jwt, so we have to implement the same in backend
// 2. Introduce expiration time for JWT, make them as short lived as possible e.g. 10-15 min. Downside is once a token is expired we have to ask user to login again. This can be terrible user experience.
// 3. Use Refresh tokens. We provide additional token called as refresh token at the time of authentication to user. We use this refresh token
//      to generate new access token whenever the access token is expired or going to expire. When user logout we just delete this
//      refresh token. Here access token is JWT token
// 4. Token Blacklisting. When user logout, we store his token in db. We check each request token in db. If token is in the db
//      then throw error as token in not valid anymore. This option defeats the purpose of using JWT, as JWT is stateless but we are maintaining
//      state in db, doing lookup in db. Instead of using RDBMS we can use in-memory db say Redis, we improve the performance of this particular solution.
// Combination of point 3 and 4 would be better solution in many use cases but in our use case, we will go with the 3rd solution.

@Service
public class JwtProvider {

    private KeyStore keyStore;

    // Expiration time for jwt. Inject from properties file
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    public String generateToken(Authentication authentication){
        User principal = (User)authentication.getPrincipal();
        // Use Jwts class to construct jwt. We set the username as subject for the jwt token
        // to sign jwt we should provide the key. compact() builds the jwt and return token in string
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                // setting the expiration time
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    // We will us asymmetric encryption. We will use key store to sign the jwt token. private key of key store to sign jwt
    private Key getPrivateKey() {
        try {
            // Read private key from key store, by passing alias and keystore password
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occured while retrieving private key from keystore", e);
        }
    }

    @PostConstruct
    public void init() {
        try {
            // We are providing KeyStore instance of type JKS to the keyStore field
            keyStore = KeyStore.getInstance("JKS");
            // Getting input stream from key store file with name springblog.jks. Create this file in resource folder
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            // We have to provide inputstream to the load() of keystore, followed by the password of keystore
            keyStore.load(resourceAsStream, "secret".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception occurred while loading keystore", e);
        }
    }

    // Validate jwt token by using public key
    public boolean validateToken(String token){
        Jwts.parser().setSigningKey(getPublicKey());
        // If token is valid true is returned else exception is thrown.
        return true;
    }

    public String getUsernameFromJwt(String token){
        // Body of token is nothing but claims
        Claims claims = Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(token).getBody();
        // Extract subject
        return claims.getSubject();
    }

    // If jwt is already expired then there will be no user information inside the security context
    // We need subject(username) while creating token, so we have created this method
    public String generateTokenWithUserName(String  username){

        // Use Jwts class to construct jwt. We set the username as subject for the jwt token
        // to sign jwt we should provide the key. compact() builds the jwt and return token in string
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(getPrivateKey())
                // setting the expiration time
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    public Long getJwtExpirationInMillis(){
        return jwtExpirationInMillis;
    }

    private PublicKey getPublicKey() {
        try {
            // Read public key from key store. The getPublicKey return public key from the key store
            return (PublicKey) keyStore.getCertificate("springblog").getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore", e);
        }
    }
}
