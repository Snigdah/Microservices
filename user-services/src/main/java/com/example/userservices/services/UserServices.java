package com.example.userservices.services;

import com.example.userservices.DTO.userRequestDto;
import com.example.userservices.DTO.userResponseDto;
import com.example.userservices.exception.ResourceNotFoundException;
import com.example.userservices.model.User;
import com.example.userservices.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServices {
    private final UserRepository userRepository;

    public UserServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create New User
    public userResponseDto createUser(userRequestDto userRequestDto){
        User user = User
                .builder()
                .name(userRequestDto.getUserName())
                .email(userRequestDto.getEmail())
                .build();

       var response = userRepository.save(user);

        return userResponseDto
                .builder()
                .userId(response.getId())
                .userName(response.getName())
                .email(response.getEmail())
                .build();
    }

    //Get All Users
    public List<userResponseDto> getAllUser(){
        List<User> user = userRepository.findAll();
        return user.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // Get user By User ID
    public User getByUser(Long userId){
        log.info("Fetching user with ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "id", userId));
    }

    private userResponseDto mapToDto(User users) {
        return userResponseDto
                .builder()
                .userId(users.getId())
                .userName(users.getName())
                .email(users.getEmail())
                .build();
    }
}
