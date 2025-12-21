package com.ssafy.crewup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.ssafy.crewup.**.mapper")
public class CrewupApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrewupApplication.class, args);
    }

}
