package cz.preclik.shop.preclikshop.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record EOrderProductDtoV1(
        @Positive @NotNull Long productId,
        @Positive @NotNull Integer quantity) { }