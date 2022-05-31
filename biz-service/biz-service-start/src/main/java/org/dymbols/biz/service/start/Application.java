package org.dymbols.biz.service.start;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@EnableDubbo(scanBasePackages = "org.dymbols.biz.service.start")
public class Application {

    public static void main(String[] args) {
        log.info("mainStart|{}|", args);

        SpringApplication.run(Application.class, args);

    }
}
