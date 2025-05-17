package com.embarkzzfitness.fitness.controller;


import com.embarkzzfitness.fitness.dto.RegisterRequest;
import com.embarkzzfitness.fitness.dto.UserResponse;
import com.embarkzzfitness.fitness.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apis/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId){
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validate(@PathVariable String userId){
        return ResponseEntity.ok(userService.existById(userId));
    }
}
