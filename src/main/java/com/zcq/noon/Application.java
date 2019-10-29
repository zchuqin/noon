package com.zcq.noon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhengchuqin
 * @version 1.0
 * @since 2019/10/08
 */
@SpringBootApplication
@MapperScan("com.zcq.noon.handler")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
