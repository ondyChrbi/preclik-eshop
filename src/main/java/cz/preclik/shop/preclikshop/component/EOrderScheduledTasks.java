package cz.preclik.shop.preclikshop.component;

import cz.preclik.shop.preclikshop.jpa.EOrderRepository;
import cz.preclik.shop.preclikshop.service.EOrderServiceV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodic tasks to check order states.
 * */
@Component
@Slf4j
@PropertySource("classpath:eshop.properties")
public class EOrderScheduledTasks {
    /**
     * Max order time in minutes to pay order. After deadline order will be marked as expired.
     * */
    @Value("${eorder.check.expired.orders.interval.minutes}")
    private Integer expirationTime = 30;

    private final EOrderServiceV1 eOrderService;

    public EOrderScheduledTasks(EOrderServiceV1 eOrderService, EOrderRepository eOrderRepository) {
        this.eOrderService = eOrderService;
    }

    /**
     * Check all orders in database and cancel those with creation date after maximum expiration time in minutes.
     * */
    @Scheduled(fixedDelay = 60000)
    public void cancelExpiredEOrders() {
        log.info("Checking expired transactions.");
        eOrderService.finishExpired(expirationTime);
    }
}
