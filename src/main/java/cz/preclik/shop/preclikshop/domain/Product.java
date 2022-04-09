package cz.preclik.shop.preclikshop.domain;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "product", indexes = {@Index(name = "product_name_idx", columnList = "name")})
public record Product(
        @Id Integer id,
        @Column String name,
        @Column String description,
        @OneToMany(mappedBy = "product", fetch = FetchType.LAZY) Collection<Price> prices,
        @OneToMany(mappedBy = "product", fetch = FetchType.LAZY) Collection<EOrderProduct> products) {

    public Product() {
        this(null, null, null, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }
}