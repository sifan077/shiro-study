package com.sifan.study;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sifan.study.mapper")
public class SecurityStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityStudyApplication.class, args);
    }

}
