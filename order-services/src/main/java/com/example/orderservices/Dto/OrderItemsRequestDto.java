package com.example.orderservices.Dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemsRequestDto {
    private long productId;
    private Integer quantity;
}
