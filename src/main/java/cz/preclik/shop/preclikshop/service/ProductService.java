package cz.preclik.shop.preclikshop.service;

import cz.preclik.shop.preclikshop.domain.EOrderProduct;
import cz.preclik.shop.preclikshop.domain.Product;
import cz.preclik.shop.preclikshop.dto.ProductDtoV1;
import cz.preclik.shop.preclikshop.service.exception.NegativeQuantityOfProductException;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;

public interface ProductService {
    /**
     * Find all products.
     *
     * @param pageable product history with paging.
     * @return entities with page information.
     */
    PagedModel findAll(Pageable pageable);

    /**
     * Find product by id.
     *
     * @param id product id.
     * @return product searched by id.
     */
    Product findById(Long id);

    /**
     * Add new product.
     *
     * @param productDto new product to be added.
     * @return added product with id.
     */
    ProductDtoV1 add(ProductDtoV1 productDto);

    /**
     * Edit product.
     *
     * @param productDto product with updated fields.
     * @param id         product id.
     * @return updated product.
     */
    ProductDtoV1 edit(ProductDtoV1 productDto, Long id);

    /**
     * Remove product.
     *
     * @param id product id.
     */
    void remove(Integer id);

    /**
     * Increase quantity of paid product.
     *
     * @param eOrderProduct product contains quantity to be increase.
     */
    void increaseQuantity(EOrderProduct eOrderProduct);

    /**
     * Increase quantity of product.
     *
     * @param productId product id.
     * @param quantity  available items.
     */
    void increaseQuantity(Long productId, Integer quantity);

    /**
     * Decrease quantity of product.
     *
     * @param product  product to decrease quantity.
     * @param quantity not available items.
     */
    void decreaseQuantity(Product product, Integer quantity) throws NegativeQuantityOfProductException;

    /**
     * Decrease quantity of product.
     *
     * @param productId product id.
     * @param quantity  not available items.
     */
    void decreaseQuantity(Long productId, Integer quantity) throws NegativeQuantityOfProductException;

    /**
     * Set quantity of product.
     *
     * @param productId product id.
     * @param quantity  not available items.
     */
    void editQuantity(Long productId, Integer quantity) throws NegativeQuantityOfProductException;
}
