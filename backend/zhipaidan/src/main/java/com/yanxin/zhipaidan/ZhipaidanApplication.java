package com.yanxin.zhipaidan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yanxin.zhipaidan.mapper")
public class ZhipaidanApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZhipaidanApplication.class, args);
    }
}
