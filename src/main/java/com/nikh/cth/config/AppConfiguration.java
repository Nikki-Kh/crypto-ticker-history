package com.nikh.cth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfiguration {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(5 * 1024 * 1024))
                .build();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}