package com.example.stock.facade;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockServiceFacade {
    private final StockService stockService;

    public StockServiceFacade(StockService stockService) {
        this.stockService = stockService;
    }

    public void decreaseStockWithOptimisticLock(Long productId, Long quantity) throws InterruptedException {
        while(true) {
            try {
                stockService.decreaseStockWithOptimisticLock(productId, quantity);
                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }

        }
    }
}
