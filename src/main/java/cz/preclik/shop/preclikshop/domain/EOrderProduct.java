package cz.preclik.shop.preclikshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "e_order_product", indexes = {
        @Index(name = "EORDER_PRODUCT_IDX", columnList = "product_id"),
        @Index(name = "EORDER_PRODUCT_ACTIVITY_IDX", columnList = "e_order_id")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EOrderProduct {
    @Id
    private Long id;
    @Column
    private Integer quantity;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private EOrder eOrder;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;
}