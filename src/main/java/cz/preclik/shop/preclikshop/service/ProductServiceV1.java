package cz.preclik.shop.preclikshop.service;

import cz.preclik.shop.preclikshop.assembler.ProductModelAssemblerV1;
import cz.preclik.shop.preclikshop.domain.EOrderProduct;
import cz.preclik.shop.preclikshop.domain.Price;
import cz.preclik.shop.preclikshop.domain.Product;
import cz.preclik.shop.preclikshop.dto.PriceDtoV1;
import cz.preclik.shop.preclikshop.dto.ProductDtoIdV1;
import cz.preclik.shop.preclikshop.dto.ProductDtoV1;
import cz.preclik.shop.preclikshop.jpa.PriceRepository;
import cz.preclik.shop.preclikshop.jpa.ProductRepository;
import cz.preclik.shop.preclikshop.service.exception.NegativeQuantityOfProductException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

/**
 * Service for creation and managing products in eshop.
 */
@Service
public class ProductServiceV1 implements ProductService {
    private final ProductRepository productRepository;

    private final PriceRepository priceRepository;

    private final PagedResourcesAssembler pagedResourcesAssembler;

    private final ProductModelAssemblerV1 productModelAssembler;

    public ProductServiceV1(ProductRepository productRepository, PriceRepository priceRepository, PagedResourcesAssembler pagedResourcesAssembler, ProductModelAssemblerV1 productModelAssembler) {
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.productModelAssembler = productModelAssembler;
    }

    @Override
    public PagedModel findAll(Pageable pageable) {
        return pagedResourcesAssembler.toModel(productRepository.findAll(pageable), productModelAssembler);
    }

    @Override
    public Product findById(final Long id) {
        return productRepository.getById(id);
    }

    @Override
    public ProductDtoIdV1 add(final ProductDtoV1 productDto) {
        Product product = productRepository.save(productFromDto(productDto));
        priceRepository.save(priceFromDto(productDto, product));

        return mapToDto(product);
    }

    @Override
    public ProductDtoIdV1 edit(final ProductDtoIdV1 productDto, final Long id) {
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
        if (quantity < 0) {
            decreaseQuantity(productId, Math.abs(quantity));
        }
        if (quantity > 0) {
            increaseQuantity(productId, quantity);
        }
    }

    @Override
    public ProductDtoIdV1 mapToDto(final Product product) {
        Price price = priceRepository.findFirstByProductEqualsOrderByValidFromDesc(product);
        PriceDtoV1 priceDto = new PriceDtoV1(price.getId(), price.getAmount(), price.getCurrency(), price.getValidFrom());

        return new ProductDtoIdV1(product.getId(), product.getName(), product.getDescription(), product.getAvailable(), product.getQuantity(), priceDto);
    }

    /**
     * Update product based on dto.
     *
     * @param productDto new product fields.
     * @param product    product to update.
     * @param price      new price.
     */
    private void update(ProductDtoIdV1 productDto, Product product, Price price) {
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
     * @return DTO as entity.
     */
    private Product productFromDto(final ProductDtoV1 productDto) {
        return new Product(null, productDto.name(), productDto.description(),
                productDto.available(), productDto.quantity(), null, null);
    }

    /**
     * Map entity from DTO to price.
     *
     * @param productDto DTO to be mapped from.
     * @param product to be associated with new order.
     * @return DTO as entity.
     */
    private Price priceFromDto(final ProductDtoIdV1 productDto, final Product product) {
        PriceDtoV1 priceDto = productDto.price();
        return new Price(null, priceDto.amount(), priceDto.currency(), priceDto.validFrom(), product);
    }

    /**
     * Map entity from DTO to price.
     *
     * @param productDto DTO to be mapped from.
     * @param product to be associated with new order.
     * @return DTO as entity.
     */
    private Price priceFromDto(final ProductDtoV1 productDto, final Product product) {
        PriceDtoV1 priceDto = productDto.price();
        return new Price(null, priceDto.amount(), priceDto.currency(), priceDto.validFrom(), product);
    }
}