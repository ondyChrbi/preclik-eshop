package cz.preclik.shop.preclikshop.dto;

import cz.preclik.shop.preclikshop.domain.EOrder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

public record EOrderDtoV1(
        @Null Long id,
        @Null EOrder.OrderState orderState,
        @NotNull Date creationDate) { }