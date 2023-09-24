package com.example.orderservices.services;

import com.example.orderservices.Dto.OrderItemsRequestDto;
import com.example.orderservices.Dto.OrderRequestDto;
import com.example.orderservices.Dto.ProductExistsRequestDto;
import com.example.orderservices.Dto.User;
import com.example.orderservices.feign.FeignCustomException;
import com.example.orderservices.feign.ProductInterface;
import com.example.orderservices.feign.UserInterface;
import com.example.orderservices.model.Order;
import com.example.orderservices.model.OrderItems;
import com.example.orderservices.repository.OrderItemsRepository;
import com.example.orderservices.repository.OrderRepository;
import com.example.orderservices.response.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServices {

    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final ProductInterface productInterface;
    private final UserInterface userInterface;
    public OrderServices(OrderRepository orderRepository,
                         OrderItemsRepository orderItemsRepository,
                         ProductInterface productInterface,
                         UserInterface userInterface) {
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.productInterface = productInterface;
        this.userInterface = userInterface;
    }

    public Object createOrder(OrderRequestDto orderRequest){

        try{
            ResponseEntity<User> user = userInterface.getUserById(orderRequest.getUserId());
        }
        catch (FeignCustomException ex){
            log.error("An error occurred during order creation", ex);
            HttpStatus statusCode = ex.getStatusCode();
            ErrorDetails errorDetails = ex.getErrorDetails();

            // Handle the error and response details as needed
            return ResponseEntity.status(statusCode)
                    .body(errorDetails);
        }

        List<ProductExistsRequestDto> orderItems = convertToProductExists(orderRequest.getOrdersList());
        try {
            ResponseEntity<?> response = productInterface.getAllProductsValidation(orderItems);

        } catch (FeignCustomException ex) {
            HttpStatus statusCode = ex.getStatusCode();
            ErrorDetails errorDetails = ex.getErrorDetails();

            // Access error details properties as needed
            String message = errorDetails.getMessage();
            String details = errorDetails.getDetails();

            // Handle the error and response details as needed
            return "Custom error: " + statusCode + ", Message: " + message + ", Details: " + details;
        }

        Order order = new Order();
        order.setUserId(orderRequest.getUserId());
        Set<OrderItems> orderItemsSet = new HashSet<>();

        for (OrderItemsRequestDto orderItemsRequestDto : orderRequest.getOrdersList()) {
            OrderItems orderItem = new OrderItems();
            orderItem.setProductId(orderItemsRequestDto.getProductId());
            orderItem.setQuantity(orderItemsRequestDto.getQuantity());
            orderItem.setOrder(order); // Associate with the order
            orderItemsSet.add(orderItem);
        }
        order.setOrdersList(orderItemsSet);

        // Save the Order and its associated OrderItems
        orderRepository.save(order);

        return "Order Create Successfully";
    }

    private List<ProductExistsRequestDto> convertToProductExists(Set<OrderItemsRequestDto> orderItemsRequestList) {
        return orderItemsRequestList.stream()
                .map(orderItemsRequestDto -> ProductExistsRequestDto.builder()
                        .productId(orderItemsRequestDto.getProductId())
                        .quantity(orderItemsRequestDto.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }


//    public String createOrder(OrderRequestDto orderRequest) {
//        try {
//            // Fetch user details from an external service using Feign client
//            ResponseEntity<User> user = userInterface.getUserById(orderRequest.getUserId());
//        } catch (FeignCustomException ex) {
//            // Handle the case where the user is not found
//            return handleCustomError(ex, "User not found with id " + orderRequest.getUserId());
//        }
//
//        // Convert OrderItemsRequestDto list to ProductExistsRequestDto list
//        List<ProductExistsRequestDto> orderItems = convertToProductExists(orderRequest.getOrdersList());
//
//        try {
//            // Validate product availability and stock levels using Feign client
//            ResponseEntity<?> response = productInterface.getAllProductsValidation(orderItems);
//        } catch (FeignCustomException ex) {
//            // Handle the case where a product is not available or out of stock
//            return handleCustomError(ex, "Product Not available or Out of stock");
//        }
//
//        // Build the Order entity
//        Order order = buildOrder(orderRequest);
//
//        // Save the Order entity to the repository
//        orderRepository.save(order);
//
//        return "Order Create Successfully";
//    }
//
//    private String handleCustomError(FeignCustomException ex, String errorMessage) {
//        // Extract the HTTP status code from the exception and create a custom error message
//        HttpStatus statusCode = ex.getStatusCode();
//        return "Custom error: " + statusCode + ", Response: " + errorMessage;
//    }
//
//    private Order buildOrder(OrderRequestDto orderRequest) {
//        // Build an Order entity using the provided OrderRequestDto
//        return Order.builder()
//                .userId(orderRequest.getUserId())
//                .ordersList(orderRequest.getOrdersList().stream()
//                        .map(this::buildOrderItem)
//                        .collect(Collectors.toSet()))
//                .build();
//    }
//
//    private OrderItems buildOrderItem(OrderItemsRequestDto orderItemsRequestDto) {
//        // Build an OrderItems entity using the provided OrderItemsRequestDto
//        return OrderItems.builder()
//                .productId(orderItemsRequestDto.getProductId())
//                .quantity(orderItemsRequestDto.getQuantity())
//                .build();
//    }
//
//    private List<ProductExistsRequestDto> convertToProductExists(Set<OrderItemsRequestDto> orderItemsRequestList) {
//        // Convert a Set of OrderItemsRequestDto to a List of ProductExistsRequestDto
//        return orderItemsRequestList.stream()
//                .map(orderItemsRequestDto -> ProductExistsRequestDto.builder()
//                        .productId(orderItemsRequestDto.getProductId())
//                        .quantity(orderItemsRequestDto.getQuantity())
//                        .build())
//                .collect(Collectors.toList());
//    }


}
