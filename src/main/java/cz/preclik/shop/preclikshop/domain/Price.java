package cz.preclik.shop.preclikshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * Price of product.
 * */
@Entity
@Table(name = "price")
@SequenceGenerator(name = "price_id_seq", sequenceName = "price_id_seq", allocationSize = 1)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "price_id_seq")
    private Long id;
    @Column
    private Double amount;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    /**
     * Defines from which date is price valid.
     * */
    @Column
    private Date validFrom;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public enum Currency {
        CZK, EUR, USD
    }
}