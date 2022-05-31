package org.dymbols.longlinks.start.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.Version;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;

import java.util.List;

public class MyClusterInvoker<T> extends AbstractClusterInvoker<T> {
 
    public MyClusterInvoker(Directory<T> directory) {
        super(directory);
    }
 
        @Override
    protected Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance)
            throws RpcException {
 
        //1.查看是否设置了指定ip
        String ip = (String) RpcContext.getContext().get("ip");
        if (StringUtils.isBlank(ip)) {
            throw new RuntimeException("ip is blank ");
        }
        //2.检查是否有可用invoker
        checkInvokers(invokers,invocation);
        
        //3.根据指定ip获取对应invoker
        Invoker<T> invoked = invokers.stream().filter(invoker -> invoker.getUrl().getHost().equals(ip))
                .findFirst().orElse(null);
        //4.检查是否有可用invoker
        if(null == invoked) {
            throw new RpcException(RpcException.NO_INVOKER_AVAILABLE_AFTER_FILTER,
                    "Failed to invoke the method " + invocation.getMethodName() + " in the service "
                            + getInterface().getName() + ". No provider available for the service "
                            + directory.getUrl().getServiceKey() + " from ip " + ip + " on the consumer "
                            + NetUtils.getLocalHost() + " using the dubbo version " + Version.getVersion()
                            + ". Please check if the providers have been started and registered.");
       }
        //5.发起远程调用，失败则抛出异常
        try {
            
            return invoked.invoke(invocation);
        } catch (Throwable e) {
            if (e instanceof RpcException && ((RpcException) e).isBiz()) { // biz exception.
                throw (RpcException) e;
            }
            throw new RpcException(e instanceof RpcException ? ((RpcException) e).getCode() : 0,
                    "Fail invoke providers " + (invoked != null?invoked.getUrl():"")+ " " + loadbalance.getClass().getSimpleName()
                            + " select from all providers " + invokers + " for service " + getInterface().getName()
                            + " method " + invocation.getMethodName() + " on consumer " + NetUtils.getLocalHost()
                            + " use dubbo version " + Version.getVersion()
                            + ", but no luck to perform the invocation. Last error is: " + e.getMessage(),
                    e.getCause() != null ? e.getCause() : e);
        }
    }
 

}