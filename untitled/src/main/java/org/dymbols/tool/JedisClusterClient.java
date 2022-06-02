package org.dymbols.tool;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class JedisClusterClient {
    private static final Logger logger = LoggerFactory.getLogger(JedisClusterClient.class);

    private static final int MAXTOTAL = 32;

    public static JedisCluster jc  = null;


    private static List<String> redisNodes = Lists.newArrayList("");
    private  static String redisPwd = "uLOf4(r0";

    public static void init() {

        try {
            logger.info("JedisClusterClient init info: redisNodes:{}| redisPwd={}", redisNodes, redisPwd);
            Set<HostAndPort> nodes = redisNodes.stream()
                    .map(node -> {
                        String[] nodeInfo = node.split(":");
                        return new HostAndPort(nodeInfo[0], Integer.valueOf(nodeInfo[1]));
                    })
                    .collect(Collectors.toSet());

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAXTOTAL);
            if(StringUtils.isNotBlank(redisPwd)){
                jc = new JedisCluster(nodes, 30000,3000,3, redisPwd,config);
            }else{
                jc = new JedisCluster(nodes, 30000,3000,3,config);
            }
            logger.info("JedisClusterClient int success...");
        }catch(Exception e){
            logger.error("JedisClusterClient",e);
            throw e;
        }
    }

    public String hget(String k, String v){
        return jc.hget(k,v);
    }

    public static Map<String,String> hgetall(String k){
        return jc.hgetAll(k);
    }

    public static Set<String> smembers(String k){
        return jc.smembers(k);
    }
}
