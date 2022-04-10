package cz.preclik.shop.preclikshop.service;

import cz.preclik.shop.preclikshop.domain.EOrder;
import cz.preclik.shop.preclikshop.domain.EOrderProduct;
import cz.preclik.shop.preclikshop.domain.Product;
import cz.preclik.shop.preclikshop.dto.EOrderDtoV1;
import cz.preclik.shop.preclikshop.dto.EOrderProductDtoV1;
import cz.preclik.shop.preclikshop.jpa.EOrderProductRepository;
import cz.preclik.shop.preclikshop.jpa.EOrderRepository;
import cz.preclik.shop.preclikshop.jpa.ProductRepository;
import cz.preclik.shop.preclikshop.service.exception.NegativeQuantityOfEOrderException;
import cz.preclik.shop.preclikshop.service.exception.NegativeQuantityOfProductException;
import cz.preclik.shop.preclikshop.service.exception.OrderCannotBeClosedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service for creation and managing order in eshop.
 * */
@Service
@Slf4j
public class EOrderServiceV1 implements EOrderService {
    private final ProductServiceV1 productService;

    private final EOrderRepository eOrderRepository;

    private final EOrderProductRepository eOrderProductRepository;

    private final ProductRepository productRepository;

    public EOrderServiceV1(ProductServiceV1 productServiceV1, EOrderRepository eOrderRepository, EOrderProductRepository eOrderProductRepository, ProductRepository productRepository) {
        this.productService = productServiceV1;
        this.eOrderRepository = eOrderRepository;
        this.eOrderProductRepository = eOrderProductRepository;
        this.productRepository = productRepository;
    }

    @Override
    public EOrderDtoV1 create(final List<EOrderProductDtoV1> products) throws NegativeQuantityOfProductException {
        EOrder entity = new EOrder(null, new Date(), EOrder.OrderState.OPEN, null);
        EOrder order = eOrderRepository.save(entity);

        for (EOrderProductDtoV1 product : products) {
            EOrderProduct orderProduct = mapTo(product, order);
            productService.decreaseQuantity(orderProduct.getProduct(), orderProduct.getQuantity());
            eOrderProductRepository.save(orderProduct);
        }

        log.info("New order (" + order.getId() + ") was created.");
        return mapTo(order);
    }

    @Override
    public void finishOrder(final Long id, final EOrder.OrderState orderState) throws OrderCannotBeClosedException {
        EOrder eOrder = eOrderRepository.findById(id).orElseThrow();

        finishOrder(eOrder, orderState);
    }

    @Override
    public void finishOrder(final EOrder eOrder, final EOrder.OrderState orderState) throws OrderCannotBeClosedException {
        if(eOrder.getOrderState().isClosed() || EOrder.OrderState.OPEN.equals(orderState)) {
            throw new OrderCannotBeClosedException(eOrder.getId());
        }

        log.info("Closing of order with id (" + eOrder.getId() + ") started.");

        if (!orderState.equals(EOrder.OrderState.FINISH)) {
            log.info("Releasing resources of order with id (" +  eOrder.getId() + ").");

            eOrderProductRepository.findAllByEOrder(eOrder)
                    .forEach(productService::increaseQuantity);
        }

        eOrder.setOrderState(orderState);
        eOrderRepository.save(eOrder);

        log.info("Order with id (" + eOrder.getId() + ") closed (" + orderState + ")");
    }

    @Override
    public void finishExpired(final Integer expirationTime) {
        eOrderRepository.findAllToBeSetAsExpirate(DateUtils.addMinutes(new Date(), expirationTime), EOrder.OrderState.OPEN)
                .forEach(eOrder -> {
                    try {
                        finishOrder(eOrder, EOrder.OrderState.EXPIRED);
                    } catch (OrderCannotBeClosedException e) {
                        e.printStackTrace();
                    }
                });

    }

    @Override
    public void edit(final Long orderId, final Long productId, final Integer count) throws NegativeQuantityOfProductException {
        EOrderProduct orderProduct = eOrderProductRepository.findOrderProductByRelatedProduct(orderId, productId).orElseThrow();
        productService.editQuantity(productId, orderProduct.getQuantity() - count);

        orderProduct.setQuantity(count);
        eOrderProductRepository.save(orderProduct);
    }

    @Override
    public void increase(final Long orderId, final Long productId, final Integer count) throws NegativeQuantityOfProductException {
        Optional<EOrderProduct> orderProductOptional = eOrderProductRepository.findOrderProductByRelatedProduct(orderId, productId);

        if (!orderProductOptional.isPresent()) {
            createOrderProduct(orderId, productId, count);
            return;
        }

        EOrderProduct orderProduct = orderProductOptional.get();
        productService.decreaseQuantity(productId, count);
        orderProduct.setQuantity(orderProduct.getQuantity() + count);

        eOrderProductRepository.save(orderProduct);
    }

    @Override
    public void decrease(final Long orderId, final Long productId, final Integer count) throws NegativeQuantityOfEOrderException {
        EOrderProduct orderProduct = eOrderProductRepository.findOrderProductByRelatedProduct(orderId, productId).orElseThrow();

        if ((orderProduct.getQuantity() - count) < 0) {
            throw new NegativeQuantityOfEOrderException(orderId);
        }

        productService.increaseQuantity(productId, count);

        if ((orderProduct.getQuantity() - count) == 0) {
            eOrderProductRepository.delete(orderProduct);
            return;
        }

        orderProduct.setQuantity(orderProduct.getQuantity() - count);
        eOrderProductRepository.save(orderProduct);
    }

    /**
     * Create new connection between order and product.
     *
     * @param orderId order id.
     * @param productId product id.
     * @param count quantity of product.
     * */
    private void createOrderProduct(Long orderId, Long productId, Integer count) {
        EOrder eOrder = eOrderRepository.findById(orderId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        eOrderProductRepository.save(new EOrderProduct(null, count, eOrder, product));
    }

    /**
     * Map entity of order to DTO.
     *
     * @param eOrder entity to be mapped.
     *
     * @return entity as dto.
     * */
    private EOrderDtoV1 mapTo(final EOrder eOrder) {
        return new EOrderDtoV1(eOrder.getId(), eOrder.getOrderState(), eOrder.getCreationDate());
    }

    /**
     * Map entity of order and product to DTO.
     *
     * @param productDtoV1 dto of product.
     * @param eOrder entity to be mapped.
     *
     * @return entity as dto.
     * */
    private EOrderProduct mapTo(final EOrderProductDtoV1 productDtoV1, final EOrder eOrder) {
        Product product = productRepository.findById(productDtoV1.productId()).orElseThrow();
        return new EOrderProduct(null, productDtoV1.quantity(), eOrder, product);
    }
}