package cz.preclik.shop.preclikshop.controller;

import cz.preclik.shop.preclikshop.annotation.v1.product.*;
import cz.preclik.shop.preclikshop.domain.Product;
import cz.preclik.shop.preclikshop.dto.ProductDtoV1;
import cz.preclik.shop.preclikshop.service.exception.NegativeQuantityOfProductException;
import cz.preclik.shop.preclikshop.service.ProductServiceV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/v1/product")
@Tag(name = "Product v1", description = "Managing products from e-shop")
public class ProductControllerV1 {
    private final ProductServiceV1 productService;

    public ProductControllerV1(ProductServiceV1 productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public ResponseEntity<PagedModel<Product>> findAll(Pageable pageable) {
        return ResponseEntity.ok()
                .contentType(MediaTypes.HAL_JSON)
                .body(productService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @FindProductEndpoint
    public ResponseEntity<Product> findById(@PathVariable("id") final Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping("")
    @AddProductEndpoint
    public ResponseEntity<ProductDtoV1> add(@RequestBody @Validated final ProductDtoV1 product) {
        return ResponseEntity.ok(productService.add(product));
    }

    @PutMapping("/{id}")
    @EditProductEndpoint
    public ResponseEntity<ProductDtoV1> edit(@RequestBody @Validated final ProductDtoV1 product, @PathVariable("id") final Long id) {
        return ResponseEntity.ok(productService.edit(product, id));
    }

    @DeleteMapping("/{id}")
    @DeleteProductEndpoint
    public ResponseEntity remove(@PathVariable("id") final Integer id){
        productService.remove(id);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/quantity/increase/{quantity}")
    @IncreaseQuantityOfProductEndpoint
    public ResponseEntity increaseQuantity(@PathVariable("id") final Long id, @PathVariable("quantity") final Integer quantity){
        productService.increaseQuantity(id, quantity);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/quantity/decrease/{count}")
    @DecreaseQuantityOfProductEndpoint
    public ResponseEntity decreaseQuantity(@PathVariable("id") final Long id, @PathVariable("count") final Integer count) throws NegativeQuantityOfProductException {
        productService.decreaseQuantity(id, count);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(value = NegativeQuantityOfProductException.class)
    private ResponseEntity<?> resourceNotFoundException(final Exception exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }
}