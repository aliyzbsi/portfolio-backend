package com.personalwebsite.portfolio.service.impl;

import com.personalwebsite.portfolio.dto.mapper.UserMapper;
import com.personalwebsite.portfolio.dto.request.AuthRequest;
import com.personalwebsite.portfolio.dto.request.RegisterRequest;
import com.personalwebsite.portfolio.dto.response.AuthResponse;
import com.personalwebsite.portfolio.dto.response.UserResponse;
import com.personalwebsite.portfolio.entity.Role;
import com.personalwebsite.portfolio.entity.User;
import com.personalwebsite.portfolio.repository.RoleRepository;
import com.personalwebsite.portfolio.repository.UserRepository;
import com.personalwebsite.portfolio.security.JwtTokenProvider;
import com.personalwebsite.portfolio.service.AuthService;
import com.personalwebsite.portfolio.util.SecurityUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;


    @PostConstruct
    public void initAdminUser(){
        if(userRepository.findByEmail(adminEmail).isPresent()){
            return;
        }
        User adminUser=new User();
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        Role admin=new Role();
        admin.setAuthority("ADMIN");
        roleRepository.save(admin);
        Role adminRole=roleRepository.findByAuthority("ADMIN")
                .orElseThrow(()->new RuntimeException("Default role not found!"));

        Set<Role> newAdmin=new HashSet<>();
        newAdmin.add(adminRole);
        adminUser.setAuthorities(newAdmin);

        userRepository.save(adminUser);

        System.out.println("Admin oluşturuldu : "+adminEmail);

    }

    @Override
    public AuthResponse login(AuthRequest request) {

        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get User - Kullanıcıyı getir
        User user= (User) authentication.getPrincipal();
        // Update last login time - Son giriş zamanını güncelle
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate token
        String token=jwtTokenProvider.genereateToken(user);
        String refreshToken=jwtTokenProvider.genereateRefreshToken(user);

        //Create response - response oluştur
        UserResponse userResponse=userMapper.toUserResponse(user);
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(userResponse)
                .message("User logged in successfully")
                .build();
    }



    @Override
    public boolean logout() {
        SecurityContextHolder.clearContext();

        return true;
    }

    @Override
    public UserResponse getCurrentUser() {
        User currentUser=securityUtils.getCurrentUser();
        return userMapper.toUserResponse(currentUser);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
       if(!jwtTokenProvider.validateToken(refreshToken)){
           throw new IllegalArgumentException("Invalid refresh token");
       }
       // Get user from token - Kullanıcıyı tokenden al
        String email=jwtTokenProvider.getUsernameFromToken(refreshToken);
       User user=userRepository.findByEmail(email)
               .orElseThrow(()->new IllegalArgumentException("User Not Found!"));

       // generate new tokens - yeni token oluştur
        String newToken=jwtTokenProvider.genereateToken(user);
        String newRefreshToken= jwtTokenProvider.genereateRefreshToken(user);

        //Create response - response oluştur
        UserResponse userResponse=userMapper.toUserResponse(user);

        return AuthResponse.builder().
                token(newToken)
                .refreshToken(newRefreshToken)
                .user(userResponse)
                .message("Token Refreshed Successfully")
                .build();

    }
}
