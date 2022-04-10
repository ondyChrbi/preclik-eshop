package cz.preclik.shop.preclikshop.service.exception;

public class NegativeQuantityOfProductException extends Throwable {
    public NegativeQuantityOfProductException(final Long id){
        super("Negative quantity of " + id +  " product");
    }
}
