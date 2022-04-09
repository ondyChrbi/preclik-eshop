package cz.preclik.shop.preclikshop.service;

import cz.preclik.shop.preclikshop.domain.EOrderProduct;
import cz.preclik.shop.preclikshop.domain.Price;
import cz.preclik.shop.preclikshop.domain.Product;
import cz.preclik.shop.preclikshop.dto.PriceDtoV1;
import cz.preclik.shop.preclikshop.dto.ProductDtoV1;
import cz.preclik.shop.preclikshop.jpa.PriceRepository;
import cz.preclik.shop.preclikshop.jpa.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public record ProductServiceV1(ProductRepository productRepository, PriceRepository priceRepository) {
    public List<ProductDtoV1> findAll() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ProductDtoV1 findById(final Long id) {
        return mapToDto(productRepository.getById(id));
    }

    public ProductDtoV1 add(final ProductDtoV1 productDto) {
        Product product = productRepository.save(productFromDto(productDto));
        priceRepository.save(fromDto(productDto, product));

        return mapToDto(product);
    }

    public ProductDtoV1 edit(final ProductDtoV1 productDto, final Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        Price price = priceRepository.save(fromDto(productDto, product));

        update(productDto, product, price);
        return mapToDto(product);
    }

    public void remove(final Integer id) {
        productRepository.setAvailable(false, id);
    }

    public void increaseQuantity(final Long id, final Integer quantity) {
        productRepository.increaseQuantity(quantity, id);
    }

    public void increaseQuantity(final EOrderProduct eOrderProduct) {
        increaseQuantity(eOrderProduct.getProduct().getId(), eOrderProduct.getQuantity());
    }

    public void decreaseQuantity(final Long id, final Integer quantity) throws NegativeQuantityOfProductException {
        Product product = productRepository.findById(id).orElseThrow();

        if ((product.getQuantity() - quantity) < 0) {
            throw new NegativeQuantityOfProductException(id);
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }

    public void editQuantity(Long productId, Integer quantity) throws NegativeQuantityOfProductException {
        if(quantity < 0) {
            decreaseQuantity(productId, quantity);
        }
        if(quantity > 0) {
            increaseQuantity(productId, quantity);
        }
    }

    private void update(ProductDtoV1 productDto, Product product, Price price) {
        product.setName(productDto.name());
        product.setDescription(productDto.description());
        product.setAvailable(productDto.available());
        product.getPrices().add(price);
    }

    private Product productFromDto(final ProductDtoV1 productDto) {
        return new Product(null, productDto.name(), productDto.description(),
                productDto.available(), productDto.quantity(), null, null);
    }

    private Price fromDto(final ProductDtoV1 productDto, final Product product) {
        PriceDtoV1 priceDto = productDto.price();
        return new Price(null, priceDto.amount(), priceDto.currency(), priceDto.validFrom(), product);
    }

    private ProductDtoV1 mapToDto(final Product product) {
        Price price = priceRepository.findFirstByProductEqualsOrderByValidFromDesc(product);
        PriceDtoV1 priceDto = new PriceDtoV1(price.getId(), price.getAmount(), price.getCurrency(), price.getValidFrom());

        return new ProductDtoV1(product.getId(), product.getName(), product.getDescription(), product.getAvailable(), product.getQuantity(), priceDto);
    }
}