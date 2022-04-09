package cz.preclik.shop.preclikshop.jpa;

import cz.preclik.shop.preclikshop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Modifying
    @Query("update Product p set p.available = :available where p.id = :id")
    Integer setAvailable(@Param("available") final Boolean available, @Param("id") Integer id);

    @Modifying
    @Query("update Product p set p.quantity = p.quantity + :quantity WHERE p.id = :id")
    Integer increaseQuantity(@Param("quantity") final Integer quantity, @Param("id") Long id);
}