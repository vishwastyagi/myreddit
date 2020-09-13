package com.mycompany.redditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutheticationResponse {

    private String authenticationToken;
    private String username;

    // Adding new fields for refresh tokens
    private String refreshToken;
    private Instant expiresAt;
}
