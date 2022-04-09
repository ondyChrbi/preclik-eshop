package cz.preclik.shop.preclikshop.jpa;

import cz.preclik.shop.preclikshop.domain.EOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EOrderRepository extends JpaRepository<EOrder, Long> {
}