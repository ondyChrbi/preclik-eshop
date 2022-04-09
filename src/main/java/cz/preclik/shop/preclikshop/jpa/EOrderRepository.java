package cz.preclik.shop.preclikshop.jpa;

import cz.preclik.shop.preclikshop.domain.EOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface EOrderRepository extends JpaRepository<EOrder, Long> {
    @Query("select eo from EOrder eo where eo.creationDate < :minusExpirationDate and eo.orderState <> :orderState")
    List<EOrder> findAllToBeSetAsExpirate(@Param("minusExpirationDate") final Date minusExpirationDate, @Param("orderState") final EOrder.OrderState orderState);
}