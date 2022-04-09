package cz.preclik.shop.preclikshop.dto;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;

public record ProductDtoV1(
        @Null Long id,
        @NotBlank @NotNull String name,
        @Nullable String description,
        @NotNull Boolean available,
        @Positive @NotNull Integer quantity,
        @NotNull PriceDtoV1 price) { }