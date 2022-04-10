package cz.preclik.shop.preclikshop.service.exception;

public class NegativeQuantityOfEOrderException extends Throwable {
    public NegativeQuantityOfEOrderException(final Long id){
        super("Negative quantity of " + id +  " order");
    }
}
