package cz.preclik.shop.preclikshop.component;

import cz.preclik.shop.preclikshop.jpa.EOrderRepository;
import cz.preclik.shop.preclikshop.service.EOrderServiceV1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:eshop.properties")
public class EOrderScheduledTasks {
    private final EOrderServiceV1 eOrderService;

    @Value("${eorder.check.expired.orders.interval.minutes}")
    private Integer expirationTime = 30;

    public EOrderScheduledTasks(EOrderServiceV1 eOrderService, EOrderRepository eOrderRepository) {
        this.eOrderService = eOrderService;
    }

    @Scheduled(fixedDelay = 5000)
    public void cancelExpiredEOrders() {
        eOrderService.finishExpired(expirationTime);
    }
}
