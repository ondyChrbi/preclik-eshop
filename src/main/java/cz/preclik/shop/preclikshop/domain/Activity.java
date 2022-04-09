package cz.preclik.shop.preclikshop.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Activity", indexes = {@Index(name = "activity_product_idx", columnList = "product_id")})
public class Activity {
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private ActivityType activityType;
    @Column
    private Integer amount;
    @Column
    private Date creationDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public enum ActivityType {
        ADD, INCREASE, DECREASE, RESERVE
    }
}