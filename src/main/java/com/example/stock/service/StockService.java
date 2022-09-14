package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /*
        기본 재고 감소 로직
     */
    @Transactional
    public void decreaseStock(Long productId, Long quantity) {
        Stock product = stockRepository.findByProductId(productId).orElseThrow();

        product.decreaseStock(quantity);

        stockRepository.saveAndFlush(product);
    }

    /*
        synchronized 키워드 를 사용하고 그 안에서 transaction 을 진행함으로 써 한 프로세스에서는 하나의 메소드가 순차적으로 실행될 수 있도록 한다.
        하지만 여러 프로세스(서버) 에서 요청이 들어올 경우 데이터베이스에 동시 접근 가능성이 있기 때문에 위험하다.
     */
    public synchronized void decreaseStockWithSynchronized(Long productId, Long quantity) {
        Stock product = stockRepository.findByProductId(productId).orElseThrow();

        product.decreaseStock(quantity);

        stockRepository.saveAndFlush(product);
    }

    /*
        pessimisticLock 을 DB에 걸어서 처리한다. 실제 exclusive lock 을 거는 행위이다.
     */
    @Transactional
    public void decreaseStockWithPerssimisticLock(Long productId, Long quantity) {
        Stock product = stockRepository.findByProductIdWithPerssimistic(productId);

        product.decreaseStock(quantity);

        stockRepository.saveAndFlush(product);
    }
}
