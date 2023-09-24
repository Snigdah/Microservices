package com.example.productservices.controller;

import com.example.productservices.DTO.ProductExistsRequestDto;
import com.example.productservices.DTO.ProductRequestDto;
import com.example.productservices.DTO.ProductResponseDto;
import com.example.productservices.response.ResponseHandler;
import com.example.productservices.services.ProductServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductServices productServices;

    public ProductController(ProductServices productServices) {
        this.productServices = productServices;
    }

    @PostMapping("/create")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto productRequest){
        ProductResponseDto response = productServices.createProduct(productRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(){
        List<ProductResponseDto> response = productServices.getAllProducts();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/internal/getAll")
    public ResponseEntity<Object> getAllProductsValidation(@RequestBody List<ProductExistsRequestDto> orderItems){
        productServices.processOrder(orderItems);
        return ResponseHandler.generateResponse("All products available", HttpStatus.OK);
    }
}
