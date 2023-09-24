package com.example.orderservices.Dto;

import com.example.orderservices.model.OrderItems;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    private long userId;
    private Set<OrderItemsRequestDto> ordersList = new HashSet<>();
}
