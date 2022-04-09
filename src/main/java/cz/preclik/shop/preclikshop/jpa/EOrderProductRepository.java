package cz.preclik.shop.preclikshop.jpa;

import cz.preclik.shop.preclikshop.domain.EOrder;
import cz.preclik.shop.preclikshop.domain.EOrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EOrderProductRepository extends JpaRepository<EOrderProduct, Long> {
    @Query("select eop from EOrderProduct eop where eop.product.id = :productId and eop.eOrder.id = :orderId")
    Optional<EOrderProduct> findOrderProductByRelatedProduct(@Param("orderId") final Long orderId, @Param("productId") final Long productId);

    @Query("select eop from EOrderProduct eop where eop.eOrder = :eOrder")
    List<EOrderProduct> findAllByEOrder(@Param("eOrder") final EOrder eorder);
}
