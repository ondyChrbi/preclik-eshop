package cz.preclik.shop.preclikshop.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "price")
public record Price(
        @Id Integer id,
        @Column Double amount,
        @Enumerated(EnumType.STRING) Currency currency,
        @Column Date validFrom,
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false) Product product) {

    public Price() {
        this(null, null, null, null, null);
    }

    public enum Currency {
        CZK, EUR, USD
    }
}