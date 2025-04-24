package com.personalwebsite.portfolio.security;

import com.personalwebsite.portfolio.entity.User;
import com.personalwebsite.portfolio.exception.ApiErrorException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token.expiration}")
    private int jwtExpirationInMs;

    @Value("${app.jwt.refresh-token.expiration}")
    private int jwtRefreshExpirationInMs;

    /**
     * Generate JWT token for a user - Bir kullanıcı için JWT token oluştur
     * @param user User for whom to generate token - Token oluşturulacak kullanıcı
     * @return JWT token
     */
    public String genereateToken(User user){
        Date now=new Date();
        Date expiryDate=new Date(now.getTime()+jwtExpirationInMs);

        Key key= Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact()
                ;
    }

    public String genereateRefreshToken(User user) {
        Date now=new Date();
        Date expiryDate=new Date(now.getTime()+jwtExpirationInMs);

        Key key= Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact()
                ;
    }
    /**
     * Validate JWT token - token i doğrula
     * @param refreshToken JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String refreshToken) {
        try {
            Key key=Keys.hmacShaKeyFor(jwtSecret.getBytes());

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    /**
     * Get username from JWT token - token den kullanıcıyı al
     * @param refreshToken JWT token
     * @return Username
     */
    public String getUsernameFromToken(String refreshToken) {
        Key key=Keys.hmacShaKeyFor(jwtSecret.getBytes());

        Claims claims=Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        return claims.getSubject();
    }

    public String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies=request.getCookies();
        if(cookies!=null){
            for (Cookie cookie:cookies){
                if("portfolio_jwt".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("portfolio_refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    public void clearAuthCookies(HttpServletResponse response) {
        // JWT token cookie'sini temizle
        Cookie jwtCookie = new Cookie("portfolio_jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/admin");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        // Refresh token cookie'sini temizle
        Cookie refreshCookie = new Cookie("portfolio_refresh_token", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/admin/auth/refresh");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }

    public void addTokenToCookie(HttpServletResponse response, String token) {
        Cookie cookie=new Cookie("portfolio_jwt",token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(jwtExpirationInMs/1000);
        cookie.setPath("/admin");
        response.addCookie(cookie);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("portfolio_refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS üzerinden çalışıyorsa true olmalı
        cookie.setMaxAge(jwtRefreshExpirationInMs / 1000); // saniye cinsinden
        cookie.setPath("/admin/auth/refresh"); // Sadece refresh endpoint'inde erişilebilir
        response.addCookie(cookie);
    }
}
