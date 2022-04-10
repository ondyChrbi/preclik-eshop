package cz.preclik.shop.preclikshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

/**
 * Product that can be bought by customer.
 * */
@Entity
@Table(name = "product", indexes = {@Index(name = "product_name_idx", columnList = "name")})
@SequenceGenerator(name = "product_id_seq", sequenceName = "product_id_seq", allocationSize = 1)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_id_seq")
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private Boolean available;
    @Column
    private Integer quantity;
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Collection<Price> prices;
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Collection<EOrderProduct> products;
}