package com.example.userservices.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class userResponseDto {
    long userId;
    String userName;
    String email;
}
