package cz.preclik.shop.preclikshop.annotation.v1.eorder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(summary = "Disable order")
@ApiResponses({
        @ApiResponse(responseCode = "204", description = "Success", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
        @ApiResponse(responseCode = "400", description = "Check your request", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
})
public @interface DisableEOrderEndpoint {
}
