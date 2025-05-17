package com.embarkzzfitness.fitness.service;

import com.embarkzzfitness.fitness.dto.RegisterRequest;
import com.embarkzzfitness.fitness.dto.UserResponse;
import com.embarkzzfitness.fitness.model.User;
import com.embarkzzfitness.fitness.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;

    public UserResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()->
                new RuntimeException("User not found"));

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setPassword(user.getPassword());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        return null;
    }

    public UserResponse register(@Valid RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("The given email is already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User saveResponse = userRepository.save(user);
        UserResponse userResponse = new UserResponse();
        userResponse.setId(saveResponse.getId());
        userResponse.setEmail(saveResponse.getEmail());
        userResponse.setPassword(saveResponse.getPassword());
        userResponse.setFirstName(saveResponse.getFirstName());
        userResponse.setLastName(saveResponse.getLastName());
        userResponse.setCreatedAt(saveResponse.getCreatedAt());
        userResponse.setUpdatedAt(saveResponse.getUpdatedAt());

        return userResponse;

    }

    public Boolean existById(String userId) {
        return userRepository.existsById(userId);
    }
}
