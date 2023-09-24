package com.example.orderservices.Dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductExistsRequestDto {
    private Long productId;
    private Integer quantity;
}
