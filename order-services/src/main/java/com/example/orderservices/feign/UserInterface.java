package com.example.orderservices.feign;

import com.example.orderservices.Dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USERS-APP", configuration = CustomFeignErrorDecoder.class)
public interface UserInterface {
    @GetMapping("/user/internal/getUserById/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId);
}
