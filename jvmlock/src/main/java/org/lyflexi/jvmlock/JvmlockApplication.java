package org.lyflexi.jvmlock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.lyflexi.jvmlock.mapper")
public class JvmlockApplication {

    public static void main(String[] args) {
        SpringApplication.run(JvmlockApplication.class, args);
    }

}
