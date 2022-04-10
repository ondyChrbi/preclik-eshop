package cz.preclik.shop.preclikshop.service;

import cz.preclik.shop.preclikshop.domain.EOrder;
import cz.preclik.shop.preclikshop.domain.EOrderProduct;
import cz.preclik.shop.preclikshop.domain.Product;
import cz.preclik.shop.preclikshop.dto.*;
import cz.preclik.shop.preclikshop.jpa.EOrderProductRepository;
import cz.preclik.shop.preclikshop.jpa.EOrderRepository;
import cz.preclik.shop.preclikshop.jpa.ProductRepository;
import cz.preclik.shop.preclikshop.service.exception.NegativeQuantityOfEOrderException;
import cz.preclik.shop.preclikshop.service.exception.NegativeQuantityOfProductException;
import cz.preclik.shop.preclikshop.service.exception.NotAvailableProductException;
import cz.preclik.shop.preclikshop.service.exception.OrderCannotBeClosedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    public EOrderCompleteDtoV1 findById(Long id) {
        EOrder eOrder = eOrderRepository.findById(id).orElseThrow();
        Collection<EOrderProductDtoV1> products = eOrder.getEOrderProducts()
                .stream()
                .map(eOrderProduct -> new EOrderProductDtoV1(
                        productService.mapToDto(eOrderProduct.getProduct()),
                        eOrderProduct.getQuantity())
                ).collect(Collectors.toList());

        return new EOrderCompleteDtoV1(eOrder.getId(), eOrder.getCreationDate(), eOrder.getOrderState(), products);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EOrderCompleteDtoV1 create(final List<EOrderProductIdDtoV1> products) throws NegativeQuantityOfProductException, NotAvailableProductException {
        EOrder eorder = new EOrder(null, new Date(), EOrder.OrderState.NOT_CREATED, null);

        List<EOrderProductDtoV1> missingProducts = checkQuantity(products);
        if (!missingProducts.isEmpty()) {
            return mapTo(eorder, missingProducts);
        }

        eorder.setOrderState(EOrder.OrderState.OPEN);

        EOrder order = eOrderRepository.save(eorder);
        decreaseQuantityOfProducts(products, order);

        log.info("New order (" + order.getId() + ") was created.");
        return mapTo(order, null);
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
    public void increase(final Long orderId, final Long productId, final Integer count) throws NegativeQuantityOfProductException, NotAvailableProductException {
        Optional<EOrderProduct> orderProductOptional = eOrderProductRepository.findOrderProductByRelatedProduct(orderId, productId);

        if (orderProductOptional.isEmpty()) {
            createOrderProduct(orderId, productId, count);
            return;
        }

        EOrderProduct orderProduct = orderProductOptional.get();

        if(!orderProduct.getProduct().getAvailable()) {
            throw new NotAvailableProductException(orderProduct.getProduct());
        }

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
    private EOrderCompleteDtoV1 mapTo(final EOrder eOrder, final Collection<EOrderProductDtoV1> productDtos) {
        return new EOrderCompleteDtoV1(eOrder.getId(), eOrder.getCreationDate(), eOrder.getOrderState(), productDtos);
    }

    /**
     * Map entity of order and product to DTO (quantity only).
     *
     * @param productDtoV1 dto of product.
     * @param eOrder entity to be mapped.
     *
     * @return entity as dto.
     * */
    private EOrderProduct mapTo(final EOrderProductIdDtoV1 productDtoV1, final EOrder eOrder) {
        Product product = productRepository.findById(productDtoV1.productId()).orElseThrow();
        return new EOrderProduct(null, productDtoV1.quantity(), eOrder, product);
    }

    /**
     * Map entity of order and product to DTO (quantity only). Order is not created yet.
     *
     * @param productDtoV1 dto of product.ed.
     *
     * @return entity as dto.
     * */
    private EOrderProduct mapTo(final EOrderProductIdDtoV1 productDtoV1) {
        Product product = productRepository.findById(productDtoV1.productId()).orElseThrow();
        return new EOrderProduct(null, productDtoV1.quantity(), null, product);
    }

    /**
     * Check if ordering products are available.
     *
     * @param eOrderProducts products to order.
     *
     * @return missing products.
     * */
    private List<EOrderProductDtoV1> checkQuantity(List<EOrderProductIdDtoV1> eOrderProducts) {
        List<EOrderProductDtoV1> missingProduct = new ArrayList<>();

        for (EOrderProductIdDtoV1 eOrderProduct : eOrderProducts) {
            EOrderProduct orderProduct = mapTo(eOrderProduct);

            Product product = orderProduct.getProduct();
            int orderingQuantity = product.getQuantity() - eOrderProduct.quantity() ;

            if (orderingQuantity < 0){
                missingProduct.add(new EOrderProductDtoV1(productService.mapToDto(orderProduct.getProduct()), Math.abs(orderingQuantity)));
            }
        }

        return missingProduct;
    }

    /**
     * Decrease quantity of ordering products.
     *
     * @param products products to order.
     * @param order order associated with products.
     * */
    private void decreaseQuantityOfProducts(List<EOrderProductIdDtoV1> products, EOrder order) throws NotAvailableProductException, NegativeQuantityOfProductException {
        for (EOrderProductIdDtoV1 product : products) {
            EOrderProduct orderProduct = mapTo(product, order);

            if(!orderProduct.getProduct().getAvailable()) {
                throw new NotAvailableProductException(orderProduct.getProduct());
            }

            productService.decreaseQuantity(orderProduct.getProduct(), orderProduct.getQuantity());
            eOrderProductRepository.save(orderProduct);

        }
    }
}