package org.dymbols.longlinks.start.config;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.io.Bytes;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;
import org.apache.dubbo.rpc.support.RpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.apache.dubbo.common.constants.CommonConstants.COMMA_SPLIT_PATTERN;
import static org.apache.dubbo.rpc.RpcContext.getClientAttachment;

/**
 * CustomLoadBalance
 */
public class CustomLoadBalance extends AbstractLoadBalance {

    private static Logger logger  = LoggerFactory.getLogger(CustomLoadBalance.class);

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        String address = getClientAttachment().getAttachment("ipT");
        logger.info("ipT|{}", address);
        logger.info("invokers|{}", invokers.size());

        for (Invoker invoker : invokers) {

            if (address == null) {
                throw new RuntimeException("must have ip");
            }
            if (address.equals(invoker.getUrl().getAddress())) {
                logger.info("match|{}", address);
                return invoker;
            }

        }
        throw new RuntimeException("CanNotFindInvoker" + address);
    }


}
