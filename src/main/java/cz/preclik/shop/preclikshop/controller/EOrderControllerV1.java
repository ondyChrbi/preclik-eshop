package cz.preclik.shop.preclikshop.controller;

import cz.preclik.shop.preclikshop.doc.v1.eorder.*;
import cz.preclik.shop.preclikshop.domain.EOrder;
import cz.preclik.shop.preclikshop.dto.EOrderDtoV1;
import cz.preclik.shop.preclikshop.dto.EOrderProductDtoV1;
import cz.preclik.shop.preclikshop.service.EOrderServiceV1;
import cz.preclik.shop.preclikshop.service.NegativeQuantityOfEOrderException;
import cz.preclik.shop.preclikshop.service.NegativeQuantityOfProductException;
import cz.preclik.shop.preclikshop.service.OrderClosedException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/order")
@Tag(name = "Order v1", description = "Ordering products from e-shop")
public class EOrderControllerV1 {
    private final EOrderServiceV1 eOrderService;

    public EOrderControllerV1(EOrderServiceV1 eOrderService) {
        this.eOrderService = eOrderService;
    }

    @PostMapping("")
    @CreateEOrderEndpoint
    public ResponseEntity<EOrderDtoV1> create(@RequestBody @Validated final List<EOrderProductDtoV1> products) throws NegativeQuantityOfProductException {
        return ResponseEntity.ok(eOrderService.create(products));
    }

    @DeleteMapping("/{id}")
    @DisableEOrderEndpoint
    public ResponseEntity disable(@PathVariable("id") final Long id) throws OrderClosedException {
        eOrderService.finishOrder(id, EOrder.OrderState.CANCEL);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/pay")
    @PayEOrderEndpoint
    public ResponseEntity pay(@PathVariable("id") final Long id) throws OrderClosedException {
        eOrderService.finishOrder(id, EOrder.OrderState.FINISH);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{orderId}/product/{productId}/quantity/edit/{count}")
    @EditEOrderEndpoint
    public ResponseEntity edit(@PathVariable("orderId") final Long orderId, @PathVariable("productId") final Long productId, @PathVariable("count") final Integer count) throws NegativeQuantityOfProductException {
        eOrderService.edit(orderId, productId, count);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{orderId}/product/{productId}/quantity/increase/{count}")
    @IncreaseEOrderEndpoint
    public ResponseEntity increase(@PathVariable("orderId") final Long orderId, @PathVariable("productId") final Long productId, @PathVariable("count") final Integer count) throws NegativeQuantityOfProductException {
        eOrderService.increase(orderId, productId, count);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{orderId}/product/{productId}/quantity/decrease/{count}")
    @DecreaseEOrderEndpoint
    public ResponseEntity decrease(@PathVariable("orderId") final Long orderId, @PathVariable("productId") final Long productId, @PathVariable("count") final Integer count) throws NegativeQuantityOfEOrderException {
        eOrderService.decrease(orderId, productId, count);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(value = NegativeQuantityOfEOrderException.class)
    private ResponseEntity<?> negativeQuantityOfEOrderHandler(final NegativeQuantityOfEOrderException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(value = NegativeQuantityOfProductException.class)
    private ResponseEntity<?> negativeQuantityOfProductHandler(final NegativeQuantityOfProductException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(value = OrderClosedException.class)
    private ResponseEntity<?> orderClosedHandler(final OrderClosedException exception) {
        return ResponseEntity.status(HttpStatus.GONE).body(exception.getMessage());
    }
}