package cz.preclik.shop.preclikshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

/**
 * Order of products. Naming e means electronic. Because some databases has order reserved as a keyword.
 **/
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

    /**
     * Define state of order.
     * */
    public enum OrderState{
        /**
         * Newly created order with 30 minutes interval to finish or cancel.
         * */
        OPEN(false),
        /**
         * Successfully paid order.
         * */
        FINISH(true),
        /**
         * Order cancelled by customer or service.
         * */
        CANCEL(true),
        /**
         * Open order expired after 30 minutes interval.
         * */
        EXPIRED(true);

        /**
         * Define if order is closed. If true, then no action is available for order.
         * */
        final boolean closed;

        OrderState(final boolean closed) {
            this.closed = closed;
        }

        public boolean isClosed() {
            return closed;
        }
    }
}