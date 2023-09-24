package com.example.userservices.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class userRequestDto {
    String userName;
    String email;
}
