package com.personalwebsite.portfolio.service;


import com.personalwebsite.portfolio.dto.request.PasswordUpdateRequest;
import com.personalwebsite.portfolio.dto.request.UserRequest;
import com.personalwebsite.portfolio.dto.response.UserResponse;

public interface UserService {



    UserResponse getCurrentUser(String username);

    void updateUserPassword( String password);
}
