package org.lyflexi.mysqlock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.lyflexi.mysqlock.mapper")
public class MysqlockApplication {

    public static void main(String[] args) {
        SpringApplication.run(MysqlockApplication.class, args);
    }

}
