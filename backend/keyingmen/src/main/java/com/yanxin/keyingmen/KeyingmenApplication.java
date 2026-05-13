package com.yanxin.keyingmen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yanxin.keyingmen.mapper")
public class KeyingmenApplication {
    public static void main(String[] args) {
        SpringApplication.run(KeyingmenApplication.class, args);
    }
}
