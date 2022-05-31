package org.dymbols.longlinks.start.controller;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.dymbols.biz.service.client.CommonService;
import org.dymbols.biz.service.client.model.ExecuteReq;
import org.dymbols.biz.service.client.model.ExecuteRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

@RestController
public class TestController {
    private Logger logger = LoggerFactory.getLogger(TestController.class);

    @DubboReference(async = true, version = "1.0.0")
    private CommonService commonService;

    private static ExecutorService executor = new ThreadPoolExecutor(10, 20,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue(10)

    );


    @GetMapping("test")
    public String test() {
        //commonService.execute(new ExecuteReq());
        // 拿到调用的Future引用，当结果返回后，会被通知和设置到此Future
        CompletableFuture<String> helloFuture = RpcContext.getContext().getCompletableFuture();
        int a = 1;
        // 为Future添加回调
        helloFuture.whenComplete((retValue, exception) -> {
            if (exception == null) {
                System.out.println(retValue);
                System.out.println(a);
                logger.info("testR|", Thread.currentThread().getName());

            } else {
                exception.printStackTrace();
            }
        });
        logger.info("test|", Thread.currentThread().getName());
        return "test";

    }

    @GetMapping("test2")
    public String test2() throws ExecutionException, InterruptedException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sss", "1231aaaa");

        executor.execute(() -> {
            JSONObject object = commonService.execute(jsonObject);
            logger.info("testR|", Thread.currentThread().getName());
        });

        logger.info("test1111");
        return "test";

    }
}
