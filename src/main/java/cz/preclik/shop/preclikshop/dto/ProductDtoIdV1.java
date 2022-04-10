package cz.preclik.shop.preclikshop.dto;

import org.springframework.lang.Nullable;

import javax.validation.constraints.*;

public record ProductDtoIdV1(
        @Null Long id,
        @NotBlank @NotNull String name,
        @Nullable String description,
        @NotNull Boolean available,
        @PositiveOrZero @NotNull Integer quantity,
        @NotNull PriceDtoV1 price) { }