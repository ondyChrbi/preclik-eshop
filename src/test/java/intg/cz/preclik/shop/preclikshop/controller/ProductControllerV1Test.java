package cz.preclik.shop.preclikshop.controller;

import cz.preclik.shop.preclikshop.domain.Price;
import cz.preclik.shop.preclikshop.domain.Product;
import cz.preclik.shop.preclikshop.jpa.PriceRepository;
import cz.preclik.shop.preclikshop.jpa.ProductRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductControllerV1Test {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        Product p1 = productRepository.save(new Product(null, "Product 1", "Description 1", true, 10, null, null));
        Product p2 = productRepository.save(new Product(null, "Product 2", "Description 2", false, 0, null, null));

        priceRepository.save(new Price(null, 10.0, Price.Currency.CZK, new Date(), p1));
        priceRepository.save(new Price(null, 0.0, Price.Currency.CZK, new Date(), p2));
    }

    @AfterEach
    void tearDown() {
        priceRepository.deleteAll();
        productRepository.deleteAll();
    }


    @Test
    void findAll() {
    }

    @Test
    void findById() {
    }

    @Test
    void add() {
    }

    @Test
    void edit() {
    }

    @Test
    void remove() {
    }

    @Test
    void increaseQuantity() {
    }

    @Test
    void decreaseQuantity() {
    }
}