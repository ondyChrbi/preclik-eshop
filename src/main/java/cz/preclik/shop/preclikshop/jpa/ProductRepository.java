package cz.preclik.shop.preclikshop.jpa;

import cz.preclik.shop.preclikshop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}