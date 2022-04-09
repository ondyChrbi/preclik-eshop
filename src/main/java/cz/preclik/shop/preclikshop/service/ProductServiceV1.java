package cz.preclik.shop.preclikshop.service;

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

    public ProductDtoV1 findById(final Integer id) {
        return mapToDto(productRepository.getById(id));
    }

    public ProductDtoV1 add(final ProductDtoV1 productDto) {
        Product product = productRepository.save(new Product(null, productDto.name(), productDto.description(),
                productDto.available(), null, null));
        priceRepository.save(fromDto(productDto, product));

        return mapToDto(product);
    }

    public void remove(final Integer id) {
        productRepository.deleteById(id);
    }

    private Price fromDto(final ProductDtoV1 productDto, final Product product) {
        PriceDtoV1 priceDto = productDto.price();
        return new Price(null, priceDto.amount(), priceDto.currency(), priceDto.validFrom(), product);
    }

    private ProductDtoV1 mapToDto(final Product product) {
        Price price = priceRepository.findFirstByProductEqualsOrderByValidFromDesc(product);
        PriceDtoV1 priceDto = new PriceDtoV1(price.getId(), price.getAmount(), price.getCurrency(), price.getValidFrom());

        return new ProductDtoV1(product.getId(), product.getName(), product.getDescription(), product.getAvailable(), priceDto);
    }
}