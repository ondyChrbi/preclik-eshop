package cz.preclik.shop.preclikshop.dto;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public record ProductDtoV1(
        @NotBlank @NotNull String name,
        @Nullable String description,
        @NotNull Boolean available,
        @PositiveOrZero @NotNull Integer quantity,
        @NotNull PriceDtoV1 price) { }