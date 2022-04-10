package cz.preclik.shop.preclikshop.service.exception;

import cz.preclik.shop.preclikshop.domain.Product;

public class NotAvailableProductException extends Throwable {
    public NotAvailableProductException(final Product product) {
        super("Product with id " + product.getId() + " is not available");
    }
}
