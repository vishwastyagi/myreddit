package com.mycompany.redditclone.controller;

import com.mycompany.redditclone.dto.AutheticationResponse;
import com.mycompany.redditclone.dto.LoginRequest;
import com.mycompany.redditclone.dto.RefreshTokenRequest;
import com.mycompany.redditclone.dto.RegisterRequest;
import com.mycompany.redditclone.service.AuthService;
import com.mycompany.redditclone.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    // Through RegisterRequest class we will be transferring user details like username,password and email as part of request body
    // We call these classes as DTO
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest){
        authService.signup(registerRequest);
        // By sending response entity, we can control what kind of response we are sending back to client
        return new ResponseEntity<>("Registration Successfull", HttpStatus.OK);
    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token){
        authService.verifyAccount(token);
        return new ResponseEntity<String>("Account activated successfully",HttpStatus.OK);
    }

    @PostMapping("/login")
    // LoginRequest is DTO
    public AutheticationResponse login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }

    // Code for refresh token
    @PostMapping("refresh/token")
    // if we add @Valid before request body then spring throws exception we you pass null or empty value for RefreshTokenRequest
    public AutheticationResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/logout")
        public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
         refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
         return  ResponseEntity.status(HttpStatus.OK).body("Refresh Token Deleted successfully");
    }

}
