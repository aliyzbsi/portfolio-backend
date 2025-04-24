package com.personalwebsite.portfolio.util;

import com.personalwebsite.portfolio.entity.User;
import com.personalwebsite.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;
    /**
     * Get the current authenticated user
     * @return User entity
     * @throws UsernameNotFoundException if user is not found
     */
    public User getCurrentUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null||!authentication.isAuthenticated()){
            throw new UsernameNotFoundException("No authenticated user found");
        }
        String email=authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found with email: "+email));
    }
    /**
     * Check if the current user is authenticated
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return authentication!=null&&authentication.isAuthenticated();
    }
    /**
     * Check if the current user has a specific role
     * @param role Role to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(String role){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return authentication!=null&&authentication.getAuthorities().stream()
                .anyMatch(authority->authority.getAuthority().equals(role));
    }

}
