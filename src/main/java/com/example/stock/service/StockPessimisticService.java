package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockPessimisticRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockPessimisticService {
    private final StockPessimisticRepository stockPessimisticRepository;

    public StockPessimisticService(StockPessimisticRepository stockPessimisticRepository) {
        this.stockPessimisticRepository = stockPessimisticRepository;
    }

    @Transactional
    public void decreaseStock(Long productId, Long quantity) {
        Stock product = stockPessimisticRepository.findByProductId(productId);

        product.decreaseStock(quantity);

        stockPessimisticRepository.saveAndFlush(product);
    }

}
