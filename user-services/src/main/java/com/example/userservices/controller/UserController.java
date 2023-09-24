package com.example.userservices.controller;

import com.example.userservices.DTO.userRequestDto;
import com.example.userservices.DTO.userResponseDto;
import com.example.userservices.model.User;
import com.example.userservices.services.UserServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserServices userServices;

    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    @PostMapping("/create")
    public ResponseEntity<userResponseDto> createUser(@RequestBody userRequestDto requestDto){
        var response = userServices.createUser(requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<userResponseDto>> getAllUsers(){
        List<userResponseDto> response = userServices.getAllUser();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/internal/getUserById/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId){
        User response = userServices.getByUser(userId);
        return ResponseEntity.ok(response);
    }
}
