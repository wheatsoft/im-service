package org.dymbols.redis;

import com.google.common.collect.Lists;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dymbols.tool.LogTests;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LettuceTests {
    private static final Logger
            LOGGER = LogManager.getLogger(LettuceTests.class);

    @Test
    public void test() throws InterruptedException {
        List<String> nodes = Lists.newArrayList("10.60.0.54:6379", "10.60.0.55:6379", "10.60.0.56:6379");
        String password = "uLOf4(r0";

        List<RedisURI> redisURIs = new ArrayList<>();
        for (String node : nodes) {
            String[] nodeInfo = node.split(":");
            redisURIs.add(RedisURI.Builder.redis(nodeInfo[0], Integer.parseInt(nodeInfo[1])).withPassword(password).build());
        }

        ClientResources clientResources = DefaultClientResources.builder()
                .ioThreadPoolSize(100)
                .computationThreadPoolSize(100)
                .build();


        RedisClusterClient client = RedisClusterClient.create(clientResources, redisURIs);
        StatefulRedisClusterConnection<String, String> connection = client.connect();
        RedisAdvancedClusterReactiveCommands<String, String> redisAdvancedClusterReactiveCommands = connection.reactive();

        System.out.println("Connected to Redis");

        ExecutorService executorService = executorService();
        for (int i = 1; i <= 100; i++) {
            executorService.execute(() -> {
                LOGGER.info("execute");

                redisAdvancedClusterReactiveCommands.get(UUID.randomUUID().toString())
                        .defaultIfEmpty("").doOnError(str -> {
                            LOGGER.error("Error|str:{}", str);})
                        .subscribe(value -> {
                            LOGGER.info("getRes|value:{}", value);
                        });
            });
        }

        Thread.sleep(100000L);
        connection.close();
        client.shutdown();
    }

    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
                100,
                100,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(100)

        );
    }
}
