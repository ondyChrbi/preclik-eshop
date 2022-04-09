package cz.preclik.shop.preclikshop.service;

public class NegativeQuantityOfProductException extends Throwable {
    public NegativeQuantityOfProductException(final Integer id){
        super("Negative quantity of " + id +  " product");
    }
}
