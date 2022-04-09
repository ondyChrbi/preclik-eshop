package cz.preclik.shop.preclikshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "e_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EOrder {
    @Id
    private Long id;
    @Column
    private Date creationDate;
    @Enumerated(EnumType.STRING)
    private OrderState orderState;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "e_order_id")
    private Collection<EOrderProduct> eOrderProducts;

    public enum OrderState{
        OPEN, FINISH, CANCEL
    }
}