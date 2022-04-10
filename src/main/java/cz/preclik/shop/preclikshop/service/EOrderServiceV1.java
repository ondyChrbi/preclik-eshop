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
import cz.preclik.shop.preclikshop.service.exception.OrderClosedException;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EOrderServiceV1 {
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

    public EOrderDtoV1 create(final List<EOrderProductDtoV1> products) throws NegativeQuantityOfProductException {
        EOrder entity = new EOrder(null, new Date(), EOrder.OrderState.OPEN, null);
        EOrder order = eOrderRepository.save(entity);

        for (EOrderProductDtoV1 product : products) {
            EOrderProduct orderProduct = mapTo(product, order);
            productService.decreaseQuantity(orderProduct.getProduct(), orderProduct.getQuantity());
            eOrderProductRepository.save(orderProduct);
        }

        return mapTo(order);
    }

    public void finishOrder(final Long id, final EOrder.OrderState orderState) throws OrderClosedException {
        EOrder eOrder = eOrderRepository.findById(id).orElseThrow();

        finishOrder(eOrder, orderState);
    }

    public void finishOrder(final EOrder eOrder, final EOrder.OrderState orderState) throws OrderClosedException {
        if(eOrder.getOrderState().isClosed()) {
            throw new OrderClosedException(eOrder.getId());
        }

        if (!orderState.equals(EOrder.OrderState.FINISH)) {
            eOrderProductRepository.findAllByEOrder(eOrder)
                    .forEach(productService::increaseQuantity);
        }

        eOrder.setOrderState(orderState);
        eOrderRepository.save(eOrder);
    }

    public void finishExpired() {
        eOrderRepository.findAllToBeSetAsExpirate(DateUtils.addMinutes(new Date(), 30), EOrder.OrderState.OPEN)
                .forEach(eOrder -> {
                    try {
                        finishOrder(eOrder, EOrder.OrderState.EXPIRED);
                    } catch (OrderClosedException e) {
                        e.printStackTrace();
                    }
                });

    }

    public void edit(final Long id, final Long productId, final Integer count) throws NegativeQuantityOfProductException {
        EOrderProduct orderProduct = eOrderProductRepository.findOrderProductByRelatedProduct(id, productId).orElseThrow();
        productService.editQuantity(productId, orderProduct.getQuantity() - count);

        orderProduct.setQuantity(count);
        eOrderProductRepository.save(orderProduct);
    }

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

    private void createOrderProduct(Long orderId, Long productId, Integer count) {
        EOrder eOrder = eOrderRepository.findById(orderId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        eOrderProductRepository.save(new EOrderProduct(null, count, eOrder, product));
    }

    private EOrderDtoV1 mapTo(final EOrder eOrder) {
        return new EOrderDtoV1(eOrder.getId(), eOrder.getOrderState(), eOrder.getCreationDate());
    }

    private EOrderProduct mapTo(final EOrderProductDtoV1 productDtoV1, final EOrder order) {
        Product product = productRepository.findById(productDtoV1.productId()).orElseThrow();
        return new EOrderProduct(null, productDtoV1.quantity(), order, product);
    }
}