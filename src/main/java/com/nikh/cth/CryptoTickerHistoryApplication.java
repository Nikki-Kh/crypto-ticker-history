package com.nikh.cth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@MapperScan("com.nikh.cth.dao")
@EnableScheduling
public class CryptoTickerHistoryApplication {

    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        run(CryptoTickerHistoryApplication.class, args);
    }

//    public static void main(String[] args) {
//        applicationContext = run(CryptoTickerHistoryApplication.class, args);
//        displayAllBeans();
//    }
//
//    public static void displayAllBeans() {
//        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
//        for(String beanName : allBeanNames) {
//            System.out.println(beanName);
//        }
//    }

}
