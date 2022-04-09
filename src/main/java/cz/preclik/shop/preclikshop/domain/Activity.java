package cz.preclik.shop.preclikshop.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Activity", indexes = {@Index(name = "activity_product_idx", columnList = "product_id")})
public record Activity(
        @Id Long id,
        @Enumerated(EnumType.STRING) ActivityType activityType,
        @Column Integer amount,
        @Column Date creationDate,
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false) Product product) {

    public Activity() {
        this(null, null, null, null, null);
    }

    public enum ActivityType {
        ADD, INCREASE, DECREASE, RESERVE
    }
}