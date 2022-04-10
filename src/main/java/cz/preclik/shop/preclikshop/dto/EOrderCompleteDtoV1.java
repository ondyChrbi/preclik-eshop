package cz.preclik.shop.preclikshop.dto;

import cz.preclik.shop.preclikshop.domain.EOrder;

import java.util.Collection;
import java.util.Date;

public record EOrderCompleteDtoV1(
        Long id,
        Date creationDate,
        EOrder.OrderState orderState,
        Collection<EOrderProductDtoV1> eOrderProducts
) { }