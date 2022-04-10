package cz.preclik.shop.preclikshop.jpa;

import cz.preclik.shop.preclikshop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Modifying
    @Transactional
    @Query("update Product p set p.available = :available where p.id = :id")
    void setAvailable(@Param("available") final Boolean available, @Param("id") Integer id);

    @Modifying
    @Transactional
    @Query("update Product p set p.quantity = p.quantity + :quantity WHERE p.id = :id")
    void increaseQuantity(@Param("quantity") final Integer quantity, @Param("id") Long id);
}