package com.nikh.cth.unit.scheduler;

import com.nikh.cth.bean.broker.Broker;
import com.nikh.cth.cache.BrokerCache;
import com.nikh.cth.dao.BrokerDao;
import com.nikh.cth.dao.TickerRateHistoryDao;
import com.nikh.cth.scheduler.TickerRateUpdateTaskDealer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {BrokerCache.class, TickerRateUpdateTaskDealer.class,
        WebClient.class, TaskScheduler.class, TickerRateHistoryDao.class, })
class TickerRateUpdateTaskDealerTest {

    @Autowired
    ApplicationContext context;
    @Autowired
    TickerRateUpdateTaskDealer tickerRateUpdateTaskDealer;
    @MockBean
    BrokerCache brokerCache;
    @MockBean
    WebClient webClient;
    @MockBean
    TaskScheduler taskScheduler;
    @MockBean
    TickerRateHistoryDao tickerRateHistoryDao;

    @Test
    void init() {
        Broker br1 = Broker.builder().brkName("Kraken").brkId(1).apiAddr("https").updInterval(100).apiKey(null).build();
        Broker br2 = Broker.builder().brkName("Kucoin").brkId(2).apiAddr("https").updInterval(100).apiKey(null).build();
        when(brokerCache.getBrokers()).thenReturn(List.of(br1, br2));
        when(brokerCache.getTickers(any())).thenReturn(List.of("t1", "t2"));
        var exec = new ScheduledThreadPoolExecutor(4);
        ScheduledFuture sFuture = exec.schedule(() -> {},0, TimeUnit.SECONDS);
        when(taskScheduler.scheduleAtFixedRate(any(),any())).thenReturn(sFuture);
        tickerRateUpdateTaskDealer.init();
        assertEquals(2, tickerRateUpdateTaskDealer.getJobsMap().size());
    }
}