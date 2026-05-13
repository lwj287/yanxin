package com.yanxin.suoguanjia;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yanxin.suoguanjia.mapper")
public class SuoguanjiaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SuoguanjiaApplication.class, args);
    }
}
