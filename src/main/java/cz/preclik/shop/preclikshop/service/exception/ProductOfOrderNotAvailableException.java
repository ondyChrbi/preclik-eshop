package cz.preclik.shop.preclikshop.service.exception;

public class ProductOfOrderNotAvailableException extends Throwable {
    public ProductOfOrderNotAvailableException() {
        super("Your order contains product which is not available. Remove it or create new order.");
    }
}
