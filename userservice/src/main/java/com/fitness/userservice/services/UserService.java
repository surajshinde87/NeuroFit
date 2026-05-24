package com.fitness.userservice.services;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.models.User;
import com.fitness.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public UserResponse register(RegisterRequest request) {


        if(userRepository.existsByEmail(request.getEmail())){
           throw new RuntimeException("Email already exists.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(request.getPassword());

        User savedUser = userRepository.save(user);
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setPassword(savedUser.getPassword());
        response.setEmail(savedUser.getEmail());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());

        return response;

    }

    public UserResponse getUserProfile(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setPassword(user.getPassword());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }
}
