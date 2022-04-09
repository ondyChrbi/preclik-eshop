package cz.preclik.shop.preclikshop.controller;

import cz.preclik.shop.preclikshop.dto.ProductDtoV1;
import cz.preclik.shop.preclikshop.service.ProductServiceV1;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/product")
public class ProductControllerV1 {
    private final ProductServiceV1 productService;

    public ProductControllerV1(ProductServiceV1 productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public List<ProductDtoV1> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ProductDtoV1 findById(@PathVariable("id") final Integer id) {
        return productService.findById(id);
    }

    @PostMapping("")
    public ResponseEntity<ProductDtoV1> add(@RequestBody @Validated final ProductDtoV1 product) {
        return ResponseEntity.ok(productService.add(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity remove(@PathVariable("id") final Integer id){
        productService.remove(id);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}