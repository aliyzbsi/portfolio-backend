package com.personalwebsite.portfolio.service;

import com.personalwebsite.portfolio.dto.request.AuthRequest;
import com.personalwebsite.portfolio.dto.request.RegisterRequest;
import com.personalwebsite.portfolio.dto.response.AuthResponse;
import com.personalwebsite.portfolio.dto.response.UserResponse;

/**
 * Service interface for authentication-related operations - Kimlik Doğrulama ile ilgili işlemler için servis arayüzü
 */
public interface AuthService {



    /**
     * Login a user - Kullanıcı girişi
     * @param request Auth request
     * @return AuthResponse containing token and user information - Token ve kullanıcı bilgilerini içeren AuthResponse
     */
    AuthResponse login(AuthRequest request);

    /**
     * Refresh an authentication token - Kimlik doğrulama tokenını yenile
     * @param refreshToken Refresh token
     * @return AuthResponse containing new token - Yeni tokenı içeren AuthResponse
     */
    AuthResponse refreshToken(String refreshToken);

    /**
     * Logout a user - Kullanıcı oturumunu sonlandır
     * @return true if logged out, false otherwise - Oturum kapatılmışsa true aksi takdirde false
     */
    boolean logout();

    /**
     * Get the current authenticated user - Aktif kullanıcı bilgisi
     * @return UserResponse
     */
    UserResponse getCurrentUser();
}
