package com.nikh.cth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.SpringApplication.run;

@EnableScheduling
@SpringBootApplication
@MapperScan("com.nikh.cth.dao")
public class CryptoTickerHistoryApplication {

    public static void main(String[] args) {
        run(CryptoTickerHistoryApplication.class, args);
    }

}
