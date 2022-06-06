package org.dymbols.tool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTests {
    private static final Logger
            LOGGER = LogManager.getLogger(LogTests.class);
    public static void main(String[] args) {
        ExecutorService executorService = new ThreadPoolExecutor(1024, 1024,
                1, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.CallerRunsPolicy());

        boolean f = true;
        for(long i=1;i<100000000000000L;i++) {

            if (i > 15000 & f) {
                try {
                    Thread.sleep(4000L);
                    f =false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
             long  b = i;
            executorService.execute(() -> {
                LOGGER.info("num:{}", b);
            });
        }

    }
}
