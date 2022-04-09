package cz.preclik.shop.preclikshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "e_order")
@SequenceGenerator(name = "e_order_id_seq", sequenceName = "e_order_id_seq", allocationSize = 1)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "e_order_id_seq")
    private Long id;
    @Column
    private Date creationDate;
    @Enumerated(EnumType.STRING)
    private OrderState orderState;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "e_order_id")
    private Collection<EOrderProduct> eOrderProducts;

    public enum OrderState{
        OPEN(false), FINISH(true), CANCEL(true), EXPIRED(true);

        final boolean closed;

        OrderState(final boolean closed) {
            this.closed = closed;
        }

        public boolean isClosed() {
            return closed;
        }
    }
}