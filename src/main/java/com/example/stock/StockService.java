package com.example.stock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void decreaseStock(Long productId, Long quantity) {
        Stock product = stockRepository.findByProductId(productId).orElseThrow();

        product.decreaseStock(quantity);

        stockRepository.saveAndFlush(product);
    }

    public synchronized void decreaseStockWithSynchronized(Long productId, Long quantity) {
        Stock product = stockRepository.findByProductId(productId).orElseThrow();

        product.decreaseStock(quantity);

        stockRepository.saveAndFlush(product);
    }
}
