package com.example.stock;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockPessimisticRepository;
import com.example.stock.repository.StockRepository;
import com.example.stock.service.StockPessimisticService;
import com.example.stock.service.StockService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootTest
public class StockTest {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockService stockService;
//    @Autowired
//    private StockPessimisticRepository stockPessimisticRepository;
//    @Autowired
//    private StockPessimisticService stockPessimisticService;

    @BeforeEach
    public void set() {
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
//        stockPessimisticRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void setAfter() {
        stockRepository.deleteAll();
//        stockPessimisticRepository.deleteAll();
    }

    @Test
    public void decrease() {
        stockService.decreaseStock(1L, 1L);
        Stock stock = stockRepository.findByProductId(1L).orElseThrow();

        Assertions.assertThat(stock.getQuantity()).isEqualTo(99L);
    }

//    @Test
//    public void 동시_요청_재고감소_perssimistic() throws InterruptedException {
//        int threadCount = 100;
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//        // CountDownLatch : 특정 쓰레드 숫자까지 다음 쓰레드를 진행하지 않도록 하기위함
//        for (int i=0; i<threadCount; i++) {
//            executorService.submit(() -> {
//                try {
//                    stockPessimisticService.decreaseStock(1L, 1L);
//                } finally {
//                    latch.countDown();
//                }
//            });
//
//        }
//
//        latch.await();
//
//        Stock stock = stockRepository.findByProductId(1L).orElseThrow();
//
//        Assertions.assertThat(stock.getQuantity()).isEqualTo(0L);
//    }

    @Test
    public void 실패케이스_동시_요청_재고감소() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        // CountDownLatch : 특정 쓰레드 숫자까지 다음 쓰레드를 진행하지 않도록 하기위함
        for (int i=0; i<threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decreaseStock(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });

        }

        latch.await();

        Stock stock = stockRepository.findByProductId(1L).orElseThrow();

        Assertions.assertThat(stock.getQuantity()).isEqualTo(0L);
    }

    @Test
    public void 성공케이스_동시_요청_재고감소_withSynchronized() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        // CountDownLatch : 특정 쓰레드 숫자까지 다음 쓰레드를 진행하지 않도록 하기위함
        for (int i=0; i<threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decreaseStockWithSynchronized(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });

        }

        latch.await();

        Stock stock = stockRepository.findByProductId(1L).orElseThrow();

        Assertions.assertThat(stock.getQuantity()).isEqualTo(0L);
    }


    @Test
    public void 실패케이스_반복문_요청_재고감소() throws InterruptedException {
        int threadCount = 100;
        for (int i=0; i<threadCount; i++) {
                    stockService.decreaseStock(1L, 1L);
        }

        Stock stock = stockRepository.findByProductId(1L).orElseThrow();
        Assertions.assertThat(stock.getQuantity()).isEqualTo(0L);
    }


}
