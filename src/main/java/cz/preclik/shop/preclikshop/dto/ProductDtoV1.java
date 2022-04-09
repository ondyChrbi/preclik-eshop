package cz.preclik.shop.preclikshop.dto;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

public record ProductDtoV1(
        @Null Integer id,
        @NotBlank @NotNull String name,
        @Nullable String description,
        @NotNull Boolean available,
        @NotNull PriceDtoV1 price) { }