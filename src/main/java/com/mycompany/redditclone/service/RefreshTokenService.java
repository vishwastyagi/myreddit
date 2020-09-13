package com.mycompany.redditclone.service;

import com.mycompany.redditclone.exception.SpringRedditException;
import com.mycompany.redditclone.model.RefreshToken;
import com.mycompany.redditclone.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

// This class is responsible to create, delete and validate refresh tokens
@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken generateRefreshToken(){
        RefreshToken refreshToken=new RefreshToken();

        // Create 128 bit random uuid
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());

        return refreshTokenRepository.save(refreshToken);
    }

    public void validateRefreshToken(String token){
        refreshTokenRepository.findByToken(token).orElseThrow(()->new SpringRedditException("Invalid Refresh Token"));
    }

    public void deleteRefreshToken(String token){
        // If token does not exist in db then spring will throw IllegalArgumentException
        refreshTokenRepository.deleteByToken(token);
    }
}
