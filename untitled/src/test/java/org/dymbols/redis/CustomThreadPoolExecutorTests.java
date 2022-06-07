package org.dymbols.redis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dymbols.tool.CustomThreadPoolExecutor;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;

public class CustomThreadPoolExecutorTests {

    private static final Logger
            LOGGER = LogManager.getLogger(ShardThreadPoolTests.class);

    public static void main(String[] args) throws InterruptedException {
        test2();
    }

    public static void test2() throws InterruptedException {
        new Thread(() -> {
            test();
        }).start();

        Thread.sleep(10000 * 100000L);
    }

    public  static void test()  {
        System.out.println(Thread.currentThread().getName());
        CustomThreadPoolExecutor executor = new CustomThreadPoolExecutor(
                100,
                ArrayBlockingQueue.class,
                2,
                Executors.defaultThreadFactory(),
                new CustomThreadPoolExecutor.CallerRunsPolicy()
        );

        String[] keys = {"a", "b", "c"};

        for (int i = 1; i < 50000000; i++) {
            final int k = i;
            final String key = keys[i % 3];

            executor.execute(new CustomThreadPoolExecutor.CustomRunnable() {
                @Override
                public String getShardKey() {
                    return key;
                }

                @Override
                public void run() {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LOGGER.info("run....|{}|i:{}", getShardKey(), k);
                }
            });
        }


    }
}
