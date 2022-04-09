package cz.preclik.shop.preclikshop.jpa;

import cz.preclik.shop.preclikshop.domain.Price;
import cz.preclik.shop.preclikshop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {
    Price findFirstByProductEqualsOrderByValidFromDesc(final Product product);
}