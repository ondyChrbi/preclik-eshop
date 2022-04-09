package cz.preclik.shop.preclikshop.domain;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "e_order")
public record EOrder(
        @Id Long id,
        @Column Date creationDate,
        @Enumerated(EnumType.STRING) OrderState orderState,
        @OneToMany(fetch = FetchType.LAZY) @JoinColumn(name = "e_order_id") Collection<EOrderProduct> eOrderProducts) {
    public enum OrderState{
        OPEN, FINISH, CANCEL
    }
}