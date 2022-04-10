package cz.preclik.shop.preclikshop.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public record EOrderProductIdDtoV1(
        @PositiveOrZero @NotNull Long productId,
        @PositiveOrZero @NotNull Integer quantity) { }