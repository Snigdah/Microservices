package com.example.orderservices.feign;

import com.example.orderservices.Dto.ProductExistsRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "PRODUCTS-APP", configuration = CustomFeignErrorDecoder.class)
public interface ProductInterface {

    @PostMapping("/product/internal/getAll")
    public ResponseEntity<?> getAllProductsValidation(@RequestBody List<ProductExistsRequestDto> orderItems);
}
