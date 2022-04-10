package cz.preclik.shop.preclikshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Connection table between product and order.
 * */
@Entity
@Table(name = "e_order_product", indexes = {
        @Index(name = "e_order_product_idx", columnList = "product_id"),
        @Index(name = "e_order_product_e_order_idx", columnList = "e_order_id")
})
@SequenceGenerator(name = "e_order_product_id_seq", sequenceName = "e_order_product_id_seq", allocationSize = 1)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EOrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "e_order_id_seq")
    private Long id;
    /**
     * Number of items available on stock.
     * */
    @Column
    private Integer quantity;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private EOrder eOrder;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;
}