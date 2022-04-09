package cz.preclik.shop.preclikshop.domain;

import javax.persistence.*;

@Entity
@Table(name="e_order_product", indexes = {
        @Index(name = "EORDER_PRODUCT_IDX", columnList = "product_id"),
        @Index(name = "EORDER_PRODUCT_ACTIVITY_IDX", columnList = "e_order_id")
})
public record EOrderProduct(
        @Id Long id,
        @Column Integer quantity,
        @ManyToOne(fetch = FetchType.LAZY, optional = false) EOrder eOrder,
        @ManyToOne(fetch = FetchType.LAZY, optional = false) Product product) {
    public EOrderProduct() {
        this(null, null, null, null);
    }
}