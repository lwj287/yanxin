package com.yanxin.zhijian;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.yanxin.zhijian.mapper")
public class ZhijianApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZhijianApplication.class, args);
    }
}
