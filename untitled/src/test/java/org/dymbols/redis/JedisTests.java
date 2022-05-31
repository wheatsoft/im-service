package org.dymbols.redis;


import org.dymbols.tool.JedisClusterClient;
import org.junit.Test;


public class JedisTests {


    @Test
    public void test() {
        JedisClusterClient.init();

        JedisClusterClient.jc.hset("testKey", "aa", "bb");

    }
}
