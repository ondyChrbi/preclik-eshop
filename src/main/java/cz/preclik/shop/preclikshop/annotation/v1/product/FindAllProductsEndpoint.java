package cz.preclik.shop.preclikshop.annotation.v1.product;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(summary = "Find all products")
public @interface FindAllProductsEndpoint {
}
