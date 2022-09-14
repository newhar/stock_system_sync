package com.example.stock.facade;

import com.example.stock.repository.RedisRepository;
import com.example.stock.repository.StockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {
    private RedisRepository redisRepository;
    private StockService stockService;

    public LettuceLockStockFacade(RedisRepository redisRepository, StockService stockService) {
        this.redisRepository = redisRepository;
        this.stockService = stockService;
    }

    /*
        spinLock 방식으로 lock을 획득할때까지 대기하였다가 획득 하면 작업을 하는 방식이다. redis 의 setnx 를 사용하여 잠금을 획득/해제 한다.
        무한정대기를 하면 레디스에 부하를 줄 수 있기 때문에 쓰레드의 sleep 을 통하여 조절해야한다.
     */
    public void decrease(Long key, Long quantity) throws InterruptedException {
        while (!redisRepository.lock(key)) {
            Thread.sleep(100);
        }

        try {
            stockService.decreaseStock(key, quantity);
        } finally {
            redisRepository.unlock(key);
        }

    }
}
