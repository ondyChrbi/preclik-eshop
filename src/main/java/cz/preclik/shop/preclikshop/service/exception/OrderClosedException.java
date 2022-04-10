package cz.preclik.shop.preclikshop.service.exception;

public class OrderClosedException extends Throwable {
    public OrderClosedException(final Long id) {
        super("Order with id " + id + " is closed.");
    }
}