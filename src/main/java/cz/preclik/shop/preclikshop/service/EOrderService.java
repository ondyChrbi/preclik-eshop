package cz.preclik.shop.preclikshop.service;

import cz.preclik.shop.preclikshop.domain.EOrder;
import cz.preclik.shop.preclikshop.dto.EOrderCompleteDtoV1;
import cz.preclik.shop.preclikshop.dto.EOrderProductIdDtoV1;
import cz.preclik.shop.preclikshop.service.exception.*;

import java.util.List;

public interface EOrderService {
    /**
     * Create new order.
     *
     * @param products initial products for order.
     * @return new order.
     * @throws NegativeQuantityOfProductException when some products will be out od stock.
     */
    EOrderCompleteDtoV1 create(List<EOrderProductIdDtoV1> products) throws NegativeQuantityOfProductException, NotAvailableProductException;

    /**
     * Finish and close order.
     *
     * @param id         Id of order to be closed.
     * @param orderState State that will be set to order. OPEN is not allowed.
     * @throws OrderCannotBeClosedException order is altery closed or wrong state passed as parameter.
     */
    void finishOrder(Long id, EOrder.OrderState orderState) throws OrderCannotBeClosedException;

    void payOrder(Long id) throws OrderCannotBeClosedException, ProductOfOrderNotAvailableException;

    /**
     * Finish and close order.
     *
     * @param eOrder     order to be close.
     * @param orderState State that will be set to order. OPEN is not allowed.
     * @throws OrderCannotBeClosedException order is altery closed or wrong state passed as parameter.
     */
    void finishOrder(EOrder eOrder, EOrder.OrderState orderState) throws OrderCannotBeClosedException;

    /**
     * Marked all transaction as expired.
     *
     * @param expirationTime minutes of deadline to expire.
     */
    void finishExpired(Integer expirationTime);

    /**
     * Edit quantity of product.
     *
     * @param orderId   order id.
     * @param productId product id.
     * @param count     new quantity of product.
     */
    void edit(Long orderId, Long productId, Integer count) throws NegativeQuantityOfProductException;

    /**
     * Increase quantity of product.
     *
     * @param orderId   order id.
     * @param productId product id.
     * @param count     new quantity of product.
     */
    void increase(Long orderId, Long productId, Integer count) throws NegativeQuantityOfProductException, NotAvailableProductException;

    /**
     * Decrease quantity of product.
     *
     * @param orderId   order id.
     * @param productId product id.
     * @param count     new quantity of product.
     */
    void decrease(Long orderId, Long productId, Integer count) throws NegativeQuantityOfEOrderException, NotAvailableProductException;

    EOrderCompleteDtoV1 findById(final Long id);
}
