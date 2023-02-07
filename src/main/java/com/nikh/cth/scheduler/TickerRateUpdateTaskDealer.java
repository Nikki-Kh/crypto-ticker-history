package com.nikh.cth.scheduler;

import com.nikh.cth.cache.BrokerCache;
import com.nikh.cth.dao.BrokerDao;
import com.nikh.cth.dao.TickerRateHistoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
public class TickerRateUpdateTaskDealer {

    private Map<String, ScheduledFuture<?>> jobsMap = new HashMap<>();

    @Autowired
    BrokerCache brokerCache;
    @Autowired
    WebClient webClient;
    @Autowired
    TaskScheduler taskScheduler;
    @Autowired
    ApplicationContext context;
    @Autowired
    TickerRateHistoryDao tickerRateHistoryDao;


    @EventListener(ApplicationReadyEvent.class)
    @Order(2)
    void init() {
        var brokers = brokerCache.getBrokers();
        brokers.forEach(broker -> {

            var tickers = brokerCache.getTickers(broker.getBrkId());
            TickerRateUpdateTask task = switch(broker.getBrkName()) {
                case "Kraken" -> new KrakenTickerRateUpdateTask(broker.getBrkId(),
                        broker.getBrkName(),
                        broker.getUpdInterval(),
                        broker.getApiAddr(),
                        broker.getApiKey(),
                        webClient,
                        tickers,
                        tickerRateHistoryDao);
                case "Kucoin" -> new KucoinTickerRateUpdateTask(broker.getBrkId(),
                        broker.getBrkName(),
                        broker.getUpdInterval(),
                        broker.getApiAddr(),
                        broker.getApiKey(),
                        webClient,
                        tickers,
                        tickerRateHistoryDao);
                default -> throw new IllegalArgumentException("fuuuu");
            };
            createBean(task);
            taskScheduler.scheduleAtFixedRate(task, Duration.ofSeconds(task.getUpdateInterval()));
        });
    }


    void createBean(TickerRateUpdateTask task) {
        ConfigurableApplicationContext configContext = (ConfigurableApplicationContext) context;
        SingletonBeanRegistry beanRegistry = configContext.getBeanFactory();
        beanRegistry.registerSingleton(task.getBrokerName() + "UpdateTask", task);
        System.out.println("added: " + configContext.getBean(task.getBrokerName() + "UpdateTask"));
    }



}
