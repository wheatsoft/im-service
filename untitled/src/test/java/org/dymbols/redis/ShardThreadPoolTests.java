package org.dymbols.redis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dymbols.tool.ShardThreadPoolExecutor;
import org.junit.Test;

public class ShardThreadPoolTests {

    private static final Logger
            LOGGER = LogManager.getLogger(ShardThreadPoolTests.class);

    @Test
    public void test() throws InterruptedException {

        String[] keys = {"a", "b", "c"};

        ShardThreadPoolExecutor executor = new ShardThreadPoolExecutor();
        for (int i = 1;i<50;i++) {

            final String key = keys[i % 3];

            executor.execute(new ShardThreadPoolExecutor.Command() {
                @Override
                public String getShardKey() {
                    return key;
                }

                @Override
                public void run() {
                    LOGGER.info("run....|{}", getShardKey());
                }
            });
        }


        Thread.sleep(10000L);
    }
}
