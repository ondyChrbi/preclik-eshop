package cz.preclik.shop.preclikshop.component;

import cz.preclik.shop.preclikshop.jpa.EOrderRepository;
import cz.preclik.shop.preclikshop.service.EOrderServiceV1;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EOrderScheduledTasks {
    private final EOrderServiceV1 eOrderService;

    public EOrderScheduledTasks(EOrderServiceV1 eOrderService, EOrderRepository eOrderRepository) {
        this.eOrderService = eOrderService;
    }

    @Scheduled(fixedDelay = 60000)
    public void cancelExpiredEOrders() {
        eOrderService.finishExpired();
    }
}
