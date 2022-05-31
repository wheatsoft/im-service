package org.dymbols.longlinks.start;


import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@EnableDubbo(scanBasePackages = "org.dymbols.longlinks.start")
public class LongLinksStart {

    public static void main(String[] args) {
        SpringApplication.run(LongLinksStart.class, args);
    }
}
