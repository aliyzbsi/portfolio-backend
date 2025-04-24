package com.personalwebsite.portfolio.dto.mapper;

import com.personalwebsite.portfolio.dto.request.PasswordUpdateRequest;
import com.personalwebsite.portfolio.dto.response.UserResponse;
import com.personalwebsite.portfolio.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user){
        if(user==null){
            return null;
        }

        return UserResponse.builder().email(user.getEmail()).build();
    }

    public void updateUserPassword(User user, PasswordUpdateRequest passwordUpdateRequest){
        if(user==null){
            return;
        }

        if(passwordUpdateRequest.getPassword()!=null){
            user.setPassword(passwordUpdateRequest.getPassword());
        }

    }
}
