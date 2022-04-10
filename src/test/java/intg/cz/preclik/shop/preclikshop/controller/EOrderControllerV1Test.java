package cz.preclik.shop.preclikshop.controller;

import cz.preclik.shop.preclikshop.domain.EOrder;
import cz.preclik.shop.preclikshop.domain.Price;
import cz.preclik.shop.preclikshop.domain.Product;
import cz.preclik.shop.preclikshop.dto.EOrderCompleteDtoV1;
import cz.preclik.shop.preclikshop.dto.EOrderProductIdDtoV1;
import cz.preclik.shop.preclikshop.jpa.EOrderProductRepository;
import cz.preclik.shop.preclikshop.jpa.EOrderRepository;
import cz.preclik.shop.preclikshop.jpa.PriceRepository;
import cz.preclik.shop.preclikshop.jpa.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EOrderControllerV1Test {
    private static final String BASE_URL = "http://localhost:%d/v1/order";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private EOrderProductRepository eOrderProductRepository;

    @Autowired
    private EOrderRepository eOrderRepository;

    @LocalServerPort
    private int port;

    @AfterEach
    void tearDown() {
        eOrderProductRepository.deleteAll();
        eOrderRepository.deleteAll();
        priceRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void postOrderWillCreateRecordToDatabase() {
        final int AMOUNT = 10;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var response = sendOrder(product, 5);
        assertTrue(eOrderRepository.findById(Objects.requireNonNull(response.getBody()).id()).isPresent());
    }

    @Test
    void postOrderWillDecreaseQuantityOfProduct() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        sendOrder(product, TO_BUY);
        assertEquals(AMOUNT - TO_BUY, productRepository.findById(product.getId()).orElseThrow().getQuantity());
    }

    @Test
    void postOrderWillProvide2xxStatus() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var response = sendOrder(product, TO_BUY);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void postOrderWithNoProductQuantityProvide2xxWithMissingProductsStatus() {
        final int AMOUNT = 10;
        final int TO_BUY = 11;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var response = sendOrder(product, TO_BUY);
        assertEquals(Objects.requireNonNull(response.getBody()).eOrderProducts().size(), 1);
    }

    @Test
    void payWillUpdateStatusInDatabaseToFinish() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var eOrder = sendOrder(product, TO_BUY).getBody();
        payOrder(Objects.requireNonNull(eOrder));

        assertEquals(EOrder.OrderState.FINISH , eOrderRepository.findById(eOrder.id()).orElseThrow().getOrderState());
    }

    @Test
    void payWillNotIncreaseProductsQuantity() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var eOrder = sendOrder(product, TO_BUY).getBody();
        payOrder(Objects.requireNonNull(eOrder));

        assertEquals(AMOUNT - TO_BUY, productRepository.findById(product.getId()).orElseThrow().getQuantity());
    }

    @Test
    void disableWillUpdateStatusInDatabaseToCancel() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var eOrder = sendOrder(product, TO_BUY).getBody();
        disableOrder(Objects.requireNonNull(eOrder));

        assertEquals(EOrder.OrderState.CANCEL , eOrderRepository.findById(eOrder.id()).orElseThrow().getOrderState());
    }

    @Test
    void disableWillIncreaseProductsQuantity() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var eOrder = sendOrder(product, TO_BUY).getBody();
        disableOrder(Objects.requireNonNull(eOrder));

        assertEquals(AMOUNT, productRepository.findById(product.getId()).orElseThrow().getQuantity());
    }

    @Test
    void editQuantityWillAffectOfEorderProduct() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;
        final int TO_SET = 10;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var eOrder = Objects.requireNonNull(sendOrder(product, TO_BUY).getBody());
        editOrder(eOrder, product, TO_SET);
        var eOrderProduct = eOrderProductRepository.findOrderProductByRelatedProduct(eOrder.id(), product.getId()).orElseThrow();

        assertEquals(TO_SET, eOrderProduct.getQuantity());
    }

    @Test
    void editPositiveQuantityWillDecreaseQuantityOfProduct() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;
        final int TO_SET = 10;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var eOrder = Objects.requireNonNull(sendOrder(product, TO_BUY).getBody());
        editOrder(eOrder, product, TO_SET);

        assertEquals(AMOUNT - TO_SET, productRepository.findById(product.getId()).orElseThrow().getQuantity());
    }

    @Test
    void editNegativeQuantityWillIncreseQuantityOfProduct() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;
        final int TO_SET = 2;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var eOrder = Objects.requireNonNull(sendOrder(product, TO_BUY).getBody());
        editOrder(eOrder, product, TO_SET);

        assertEquals(AMOUNT - TO_SET, productRepository.findById(product.getId()).orElseThrow().getQuantity());
    }

    @Test
    void editPositiveQuantityOutOfStockWillNotAffectQuantityOfProduct() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;
        final int TO_SET = 11;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var eOrder = Objects.requireNonNull(sendOrder(product, TO_BUY).getBody());
        editOrder(eOrder, product, TO_SET);

        assertEquals(AMOUNT - TO_BUY, productRepository.findById(product.getId()).orElseThrow().getQuantity());
    }

    @Test
    void increaseQuantityOfEorderProductWillDecreaseQuantityOfProduct() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;
        final int TO_TO_BUY_NEXT = 5;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var eOrder = Objects.requireNonNull(sendOrder(product, TO_BUY).getBody());
        increaseProductOfOrder(eOrder, product, TO_TO_BUY_NEXT);

        assertEquals(AMOUNT - TO_BUY - TO_TO_BUY_NEXT, productRepository.findById(product.getId()).orElseThrow().getQuantity());
    }


    @Test
    void increaseQuantityOutOfStockWillNotAffectQuantityOfProduct() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;
        final int TO_TO_BUY_NEXT = 11;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var eOrder = Objects.requireNonNull(sendOrder(product, TO_BUY).getBody());
        increaseProductOfOrder(eOrder, product, TO_TO_BUY_NEXT);

        assertEquals(AMOUNT - TO_BUY, productRepository.findById(product.getId()).orElseThrow().getQuantity());
    }

    @Test
    void decreaseQuantityOfEorderProductWillDecreaseQuantityOfProduct() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;
        final int TO_REMOVE_NEXT = 2;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var eOrder = Objects.requireNonNull(sendOrder(product, TO_BUY).getBody());
        decreaseProductOfOrder(eOrder, product, TO_REMOVE_NEXT);

        assertEquals(AMOUNT - TO_BUY + TO_REMOVE_NEXT, productRepository.findById(product.getId()).orElseThrow().getQuantity());
    }

    @Test
    void increaseNotKnowProductWillAddItToOrder() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        Product product2 = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var eOrder = Objects.requireNonNull(sendOrder(product, TO_BUY).getBody());
        increaseProductOfOrder(eOrder, product2, TO_BUY);

        var eOrderProduct = eOrderProductRepository.findOrderProductByRelatedProduct(eOrder.id(), product2.getId()).orElseThrow();
        assertEquals(TO_BUY, eOrderProduct.getQuantity());
    }

    @Test
    void decreaseProductQuantityToZeroWillRemoveItFromEOrder() {
        final int AMOUNT = 10;
        final int TO_BUY = 5;

        Product product = productRepository.save(new Product(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, AMOUNT, null, null));
        priceRepository.save(new Price(null, new Random().nextDouble(), Price.Currency.CZK, new Date(), product));

        var eOrder = Objects.requireNonNull(sendOrder(product, TO_BUY).getBody());
        decreaseProductOfOrder(eOrder, product, TO_BUY);

        var eOrderProduct = eOrderProductRepository.findOrderProductByRelatedProduct(eOrder.id(), product.getId());
        assertFalse(eOrderProduct.isPresent());
    }

    private ResponseEntity<EOrderCompleteDtoV1> sendOrder(final Product product, final int toBuy) {
        EOrderProductIdDtoV1 eOrderProductIdDtoV1 = new EOrderProductIdDtoV1(product.getId(), toBuy);
        HttpEntity<List<EOrderProductIdDtoV1>> request = new HttpEntity<>(Collections.singletonList(eOrderProductIdDtoV1), new HttpHeaders());

        return restTemplate.postForEntity(String.format(BASE_URL, port), request, EOrderCompleteDtoV1.class);
    }

    private void payOrder(final EOrderCompleteDtoV1 orderDtoV1) {
        HttpEntity<List<EOrderProductIdDtoV1>> request = new HttpEntity<>(null, new HttpHeaders());

        restTemplate.put(String.format(BASE_URL + "/" + orderDtoV1.id() + "/pay" , port), request);
    }

    private void disableOrder(final EOrderCompleteDtoV1 orderDtoV1) {
        HttpEntity<List<EOrderProductIdDtoV1>> request = new HttpEntity<>(null, new HttpHeaders());

        restTemplate.delete(String.format(BASE_URL + "/" + orderDtoV1.id() , port), request);
    }

    private void editOrder(final EOrderCompleteDtoV1 orderDtoV1, final Product product, final int quantity) {
        String url = String.format("%s/%d/product/%d/quantity/edit/%d", String.format(BASE_URL, port), orderDtoV1.id(), product.getId(), quantity);

        HttpEntity<List<EOrderProductIdDtoV1>> request = new HttpEntity<>(null, new HttpHeaders());
        restTemplate.put(String.format(url , port), request);
    }

    private void increaseProductOfOrder(final EOrderCompleteDtoV1 orderDtoV1, final Product product, final int quantity) {
        String url = String.format("%s/%d/product/%d/quantity/increase/%d", String.format(BASE_URL, port), orderDtoV1.id(), product.getId(), quantity);

        HttpEntity<List<EOrderProductIdDtoV1>> request = new HttpEntity<>(null, new HttpHeaders());
        restTemplate.put(String.format(url , port), request);
    }

    private void decreaseProductOfOrder(final EOrderCompleteDtoV1 orderDtoV1, final Product product, final int quantity) {
        String url = String.format("%s/%d/product/%d/quantity/decrease/%d", String.format(BASE_URL, port), orderDtoV1.id(), product.getId(), quantity);

        HttpEntity<List<EOrderProductIdDtoV1>> request = new HttpEntity<>(null, new HttpHeaders());
        restTemplate.put(String.format(url , port), request);
    }
}