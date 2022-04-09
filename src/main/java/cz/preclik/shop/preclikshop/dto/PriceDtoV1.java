package cz.preclik.shop.preclikshop.dto;

import cz.preclik.shop.preclikshop.domain.Price;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Date;

public record PriceDtoV1(
        @Nullable Long id,
        @NotNull Double amount,
        @NotNull Price.Currency currency,
        @NotNull Date validFrom) {
}