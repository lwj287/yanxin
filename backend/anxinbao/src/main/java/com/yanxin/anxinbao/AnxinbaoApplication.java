package com.yanxin.anxinbao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yanxin.anxinbao.mapper")
public class AnxinbaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnxinbaoApplication.class, args);
    }
}