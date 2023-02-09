package com.nikh.cth.scheduler;

import com.nikh.cth.cache.BrokerCache;
import com.nikh.cth.dao.TickerRateHistoryDao;
import com.nikh.cth.error.ServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
public class TickerRateUpdateTaskDealer {

    private Map<String, ScheduledFuture<?>> jobsMap;

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


    @EventListener(value = ApplicationReadyEvent.class,
            condition = "@environment.getActiveProfiles()[0] != 'test'")
    @Order(2)
    public void init() {
        if (jobsMap != null) {
            return;
        }
        jobsMap = new HashMap<>();
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
                default -> throw new ServerException("Broker is not supported");
            };
            createBean(task);
            var scheduledFuture = taskScheduler.scheduleAtFixedRate(task, Duration.ofSeconds(task.getUpdateInterval()));
            jobsMap.put(broker.getBrkName(), scheduledFuture);
        });
    }


    private void createBean(TickerRateUpdateTask task) {
        ConfigurableApplicationContext configContext = (ConfigurableApplicationContext) context;
        SingletonBeanRegistry beanRegistry = configContext.getBeanFactory();
        beanRegistry.registerSingleton(task.getBrokerName() + "UpdateTask", task);
    }

    public Map<String, ScheduledFuture<?>> getJobsMap() {
        return jobsMap;
    }
}
