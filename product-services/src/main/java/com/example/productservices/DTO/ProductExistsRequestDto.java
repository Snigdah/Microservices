package com.example.productservices.DTO;

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
