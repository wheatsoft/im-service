package org.dymbols.longlinks.start.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dymbols.biz.service.client.CommonService;
import org.dymbols.biz.service.client.OrderService;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.dubbo.rpc.RpcContext.getClientAttachment;

@RestController
public class RouterController {

    @DubboReference(version = "1.0.0", retries = 1)
    private CommonService commonService;

    @DubboReference(version = "1.0.0")
    private OrderService orderService;

    @GetMapping("router")
    public String router(String ip) throws InterruptedException {
        getClientAttachment().setAttachment("ipT", ip);
        JSONObject jsonObject = commonService.execute(new JSONObject());

        getClientAttachment().setAttachment("ipT", ip);
        orderService.queryOrderInfo();
        return jsonObject.toJSONString();
    }

}
