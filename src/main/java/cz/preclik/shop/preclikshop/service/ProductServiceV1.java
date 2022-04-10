package cz.preclik.shop.preclikshop.service;

import cz.preclik.shop.preclikshop.assembler.ProductModelAssemblerV1;
import cz.preclik.shop.preclikshop.domain.EOrderProduct;
import cz.preclik.shop.preclikshop.domain.Price;
import cz.preclik.shop.preclikshop.domain.Product;
import cz.preclik.shop.preclikshop.dto.PriceDtoV1;
import cz.preclik.shop.preclikshop.dto.ProductDtoV1;
import cz.preclik.shop.preclikshop.jpa.PriceRepository;
import cz.preclik.shop.preclikshop.jpa.ProductRepository;
import cz.preclik.shop.preclikshop.service.exception.NegativeQuantityOfProductException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

/**
 * Service for creation and managing products in eshop.
 * */
@Service
public record ProductServiceV1(ProductRepository productRepository, PriceRepository priceRepository,
                               PagedResourcesAssembler pagedResourcesAssembler, ProductModelAssemblerV1 productModelAssembler) implements ProductService {
    @Override
    public PagedModel findAll(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);

        return pagedResourcesAssembler.toModel(page, productModelAssembler);
    }

    @Override
    public Product findById(final Long id) {
        return productRepository.getById(id);
    }

    @Override
    public ProductDtoV1 add(final ProductDtoV1 productDto) {
        Product product = productRepository.save(productFromDto(productDto));
        priceRepository.save(priceFromDto(productDto, product));

        return mapToDto(product);
    }

    @Override
    public ProductDtoV1 edit(final ProductDtoV1 productDto, final Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        Price price = priceRepository.save(priceFromDto(productDto, product));

        update(productDto, product, price);
        return mapToDto(product);
    }

    @Override
    public void remove(final Integer id) {
        productRepository.setAvailable(false, id);
    }

    @Override
    public void increaseQuantity(final EOrderProduct eOrderProduct) {
        increaseQuantity(eOrderProduct.getProduct().getId(), eOrderProduct.getQuantity());
    }

    @Override
    public void increaseQuantity(final Long productId, final Integer quantity) {
        productRepository.increaseQuantity(quantity, productId);
    }

    @Override
    public void decreaseQuantity(final Product product, final Integer quantity) throws NegativeQuantityOfProductException {
        decreaseQuantity(product.getId(), quantity);
    }

    @Override
    public void decreaseQuantity(final Long productId, final Integer quantity) throws NegativeQuantityOfProductException {
        Product product = productRepository.findById(productId).orElseThrow();

        if ((product.getQuantity() - quantity) < 0) {
            throw new NegativeQuantityOfProductException(productId);
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }

    @Override
    public void editQuantity(Long productId, Integer quantity) throws NegativeQuantityOfProductException {
        if(quantity < 0) {
            decreaseQuantity(productId, Math.abs(quantity));
        }
        if(quantity > 0) {
            increaseQuantity(productId, quantity);
        }
    }

    /**
     * Update product based on dto.
     *
     * @param productDto new product fields.
     * @param product product to update.
     * @param price new price.
     * */
    private void update(ProductDtoV1 productDto, Product product, Price price) {
        product.setName(productDto.name());
        product.setDescription(productDto.description());
        product.setAvailable(productDto.available());
        if (price != null) {
            product.getPrices().add(price);
        }
    }

    /**
     * Map entity from DTO to product.
     *
     * @param productDto DTO to be mapped.
     *
     * @return DTO as entity.
     * */
    private Product productFromDto(final ProductDtoV1 productDto) {
        return new Product(null, productDto.name(), productDto.description(),
                productDto.available(), productDto.quantity(), null, null);
    }

    /**
     * Map entity from DTO to price.
     *
     * @param productDto DTO to be mapped from.
     * @param
     *
     * @return DTO as entity.
     * */
    private Price priceFromDto(final ProductDtoV1 productDto, final Product product) {
        PriceDtoV1 priceDto = productDto.price();
        return new Price(null, priceDto.amount(), priceDto.currency(), priceDto.validFrom(), product);
    }

    /**
     * Map entity of product to DTO.
     *
     * @param product entity to be mapped.
     *
     * @return entity as dto.
     * */
    private ProductDtoV1 mapToDto(final Product product) {
        Price price = priceRepository.findFirstByProductEqualsOrderByValidFromDesc(product);
        PriceDtoV1 priceDto = new PriceDtoV1(price.getId(), price.getAmount(), price.getCurrency(), price.getValidFrom());

        return new ProductDtoV1(product.getId(), product.getName(), product.getDescription(), product.getAvailable(), product.getQuantity(), priceDto);
    }
}