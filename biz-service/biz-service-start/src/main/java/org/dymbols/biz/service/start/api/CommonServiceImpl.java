package org.dymbols.biz.service.start.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.dymbols.biz.service.client.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;


@DubboService(version = "1.0.0")
@Slf4j
public class CommonServiceImpl implements CommonService {

    private Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Value("${dubbo.protocol.port}")
    private String port;

    @Override
    public JSONObject execute(JSONObject req)  {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("port", port);
        logger.info("req:{}", req);
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public JSONObject execute2(JSONObject req) {
        return null;
    }
}
