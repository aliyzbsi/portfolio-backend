package com.personalwebsite.portfolio.controller;

import com.personalwebsite.portfolio.dto.request.AuthRequest;
import com.personalwebsite.portfolio.dto.response.ApiResponse;
import com.personalwebsite.portfolio.dto.response.AuthResponse;
import com.personalwebsite.portfolio.dto.response.UserResponse;
import com.personalwebsite.portfolio.security.JwtTokenProvider;
import com.personalwebsite.portfolio.service.AuthService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest, HttpServletResponse response){
        AuthResponse authResponse=authService.login(authRequest);

        tokenProvider.addTokenToCookie(response,authResponse.getToken());
        tokenProvider.addRefreshTokenCookie(response,authResponse.getRefreshToken());

        return ResponseEntity.ok(authResponse);

    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        String refreshToken=tokenProvider.getRefreshTokenFromCookie(request);
        if(refreshToken==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
            AuthResponse authResponse=authService.refreshToken(refreshToken);

            tokenProvider.addTokenToCookie(response,authResponse.getToken());
            tokenProvider.addRefreshTokenCookie(response,authResponse.getRefreshToken());

            return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response){
        authService.logout();
        tokenProvider.clearAuthCookies(response);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }
}
