package com.example.orderservices.controller;

import com.example.orderservices.Dto.OrderRequestDto;
import com.example.orderservices.Dto.ProductExistsRequestDto;
import com.example.orderservices.services.OrderServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderServices orderServices;

    public OrderController(OrderServices orderServices) {
        this.orderServices = orderServices;
    }

    @PostMapping("/create")
    public Object createOrder(@RequestBody OrderRequestDto orderRequest) {
        return orderServices.createOrder(orderRequest);
    }
}
