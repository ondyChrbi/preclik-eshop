package cz.preclik.shop.preclikshop.doc.v1.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(summary = "Decrease quantity of product")
@ApiResponses({
        @ApiResponse(responseCode = "204", description = "Success", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
        @ApiResponse(responseCode = "400", description = "Check your request", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
})
public @interface DecreaseQuantityOfProductEndpoint {
}
