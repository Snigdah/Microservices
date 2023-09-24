package com.example.productservices.services;

import com.example.productservices.DTO.ProductExistsRequestDto;
import com.example.productservices.DTO.ProductRequestDto;
import com.example.productservices.DTO.ProductResponseDto;
import com.example.productservices.exception.ResourceNotFoundException;
import com.example.productservices.model.Product;
import com.example.productservices.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServices {
    private final ProductRepository productRepository;

    public ProductServices(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDto createProduct(ProductRequestDto productResponseDto) {
        Product product = Product
                .builder()
                .productName(productResponseDto.getProductName())
                .quantity(productResponseDto.getQuantity())
                .build();

        var data = productRepository.save(product);

        return ProductResponseDto
                .builder()
                .id(data.getId())
                .productName(data.getProductName())
                .quantity(data.getQuantity())
                .build();
    }

    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void processOrder(List<ProductExistsRequestDto> orderItems) {
        Set<Product> updatedProducts = orderItems.stream()
                .map(this::validateAndPrepareProduct)
                .collect(Collectors.toSet());

        productRepository.saveAll(updatedProducts);
    }

    private Product validateAndPrepareProduct(ProductExistsRequestDto item) {
        Long productId = item.getProductId();
        Integer quantity = item.getQuantity();

        // Check if the product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "product", productId));

        // Check if the available quantity is sufficient
        if (product.getQuantity() < quantity) {
            throw new ResourceNotFoundException("Product", "product", productId);
        }

        // Update the product quantity in memory
        int updatedQuantity = product.getQuantity() - quantity;
        product.setQuantity(updatedQuantity);

        return product;
    }


//    public boolean isOrderValid(List<ProductExistsRequestDto> orderItems) {
//        boolean allItemsValid = orderItems.stream()
//                .allMatch(item -> isItemValid(item));
//
//        Set<Product> updatedProducts = new HashSet<>();
//
//        for(ProductExistsRequestDto orderItem : orderItems){
//            Long productId = orderItem.getProductId();
//            Integer quantity = orderItem.getQuantity();
//
//            Product product = productRepository.findById(productId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Product", "product", productId));
//
//            int updatedQuantity = product.getQuantity() - quantity;
//            product.setQuantity(updatedQuantity);
//            updatedProducts.add(product);
//        }
//        productRepository.saveAll(updatedProducts);
//        return allItemsValid;
//    }
//
//    private boolean isItemValid(ProductExistsRequestDto item) {
//        Long productId = item.getProductId();
//        Integer quantity = item.getQuantity();
//
//        // Check if the product exists
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Product", "product", productId));
//
//        // Check if the available quantity is sufficient
//        if (product.getQuantity() < quantity) {
//            throw new ResourceNotFoundException("Product", "product", productId);
//        }
//
//        return true;
//    }

    private ProductResponseDto mapToDTO(Product data) {
        return ProductResponseDto
                .builder()
                .id(data.getId())
                .productName(data.getProductName())
                .quantity(data.getQuantity())
                .build();
    }
}
