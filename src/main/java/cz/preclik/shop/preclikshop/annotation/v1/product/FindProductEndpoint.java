package cz.preclik.shop.preclikshop.annotation.v1.product;

import cz.preclik.shop.preclikshop.dto.ProductDtoIdV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(summary = "Find product based on id")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success", content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = ProductDtoIdV1.class)))),
        @ApiResponse(responseCode = "400", description = "Check your request", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
})
public @interface FindProductEndpoint {
}
