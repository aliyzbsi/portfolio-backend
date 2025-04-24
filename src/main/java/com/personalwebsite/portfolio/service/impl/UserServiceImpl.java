package com.personalwebsite.portfolio.service.impl;

import com.personalwebsite.portfolio.dto.mapper.UserMapper;
import com.personalwebsite.portfolio.dto.request.PasswordUpdateRequest;
import com.personalwebsite.portfolio.dto.request.UserRequest;
import com.personalwebsite.portfolio.dto.response.UserResponse;
import com.personalwebsite.portfolio.entity.User;
import com.personalwebsite.portfolio.repository.UserRepository;
import com.personalwebsite.portfolio.service.UserService;
import com.personalwebsite.portfolio.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final UserMapper userMapper;
    @Override
    public UserResponse getCurrentUser(String username) {
        User currentuser=securityUtils.getCurrentUser();
        return userMapper.toUserResponse(currentuser);
    }

    @Override
    public void updateUserPassword(String password) {
        User currentUser=securityUtils.getCurrentUser();
        if(currentUser!=null){
            PasswordUpdateRequest passwordUpdateRequest=new PasswordUpdateRequest();
            passwordUpdateRequest.setPassword(password);
        }
        
    }

}
