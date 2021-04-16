package com.qqkj.inspection;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@MapperScan("com.qqkj.inspection.inspection.mapper")
public class InspectionApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(InspectionApplication.class).run(args);
    }
}
