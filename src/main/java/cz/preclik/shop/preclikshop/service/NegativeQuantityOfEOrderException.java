package cz.preclik.shop.preclikshop.service;

public class NegativeQuantityOfEOrderException extends Throwable {
    public NegativeQuantityOfEOrderException(final Long id){
        super("Negative quantity of " + id +  " order");
    }
}
