package cz.preclik.shop.preclikshop.dto;

import cz.preclik.shop.preclikshop.domain.Price;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

public record PriceDtoV1(
        @Null Integer id,
        @NotNull Double amount,
        @NotNull Price.Currency currency,
        @NotNull Date validFrom) { }