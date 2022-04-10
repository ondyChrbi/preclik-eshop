package cz.preclik.shop.preclikshop.service.exception;

public class OrderCannotBeClosedException extends Throwable {
    public OrderCannotBeClosedException(final Long id) {
        super("Order with id " + id + " is closed.");
    }
}
