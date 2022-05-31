package org.dymbols.biz.service.start.api;

import org.apache.dubbo.config.annotation.DubboService;
import org.dymbols.biz.service.client.OrderService;

@DubboService(version = "1.0.0")
public class OrderServiceImpl implements OrderService {
    @Override
    public String queryOrderInfo() {
        return null;
    }

    @Override
    public String queryOrderInfo2() {
        return null;
    }
}
