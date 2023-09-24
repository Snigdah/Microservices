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
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    private final CircuitBreaker circuitBreaker;


    public OrderServices(OrderRepository orderRepository,
                         OrderItemsRepository orderItemsRepository,
                         ProductInterface productInterface,
                         UserInterface userInterface, CircuitBreaker circuitBreaker) {
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.productInterface = productInterface;
        this.userInterface = userInterface;
        this.circuitBreaker = CircuitBreaker.ofDefaults("orderCircuitBreaker");
    }

    public Object createOrder(OrderRequestDto orderRequest){

        try{
//            ResponseEntity<User> user = circuitBreaker.executeSupplier(() -> userInterface.getUserById(orderRequest.getUserId()),
//                    throwable -> getUserByIdFallback(orderRequest.getUserId(), throwable));

            ResponseEntity<User> user = userInterface.getUserById(orderRequest.getUserId());
        }
        catch (FeignCustomException ex){
            log.error("An error occurred during order creation", ex);
            return handleFeignException(ex);
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

//    private ResponseEntity<User> getUserById(Long userId) {
//        return userInterface.getUserById(userId);
//    }

    private ResponseEntity<ErrorDetails> handleFeignException(FeignCustomException ex) {
        HttpStatus statusCode = ex.getStatusCode();
        ErrorDetails errorDetails = ex.getErrorDetails();

        // Handle the error and response details as needed
        return ResponseEntity.status(statusCode)
                .body(errorDetails);
    }


    private ResponseEntity<User> getUserByIdFallback(long userId, Throwable ex) {
        // Implement your custom fallback logic here
        log.error("Circuit is open or an error occurred while fetching user by ID", ex);

        // You can return a default or custom ResponseEntity<User> here
        // For example:
        User user = new User();
        user.setId(userId);
        user.setName("Fallback User");
        return ResponseEntity.ok(user);
    }

    private List<ProductExistsRequestDto> convertToProductExists(Set<OrderItemsRequestDto> orderItemsRequestList) {
        return orderItemsRequestList.stream()
                .map(orderItemsRequestDto -> ProductExistsRequestDto.builder()
                        .productId(orderItemsRequestDto.getProductId())
                        .quantity(orderItemsRequestDto.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

}
