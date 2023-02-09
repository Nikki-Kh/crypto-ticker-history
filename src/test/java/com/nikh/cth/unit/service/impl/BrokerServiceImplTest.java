package com.nikh.cth.unit.service.impl;

import com.nikh.cth.bean.broker.Broker;
import com.nikh.cth.cache.BrokerCache;
import com.nikh.cth.service.impl.BrokerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {BrokerServiceImpl.class, BrokerCache.class})
class BrokerServiceImplTest {

    @Autowired
    BrokerServiceImpl brokerService;

    @MockBean
    BrokerCache brokerCache;

    List<Broker> brokers;

    @BeforeEach
    void init() {
        var brk1 = Broker.builder().brkId(1).brkName("Kraken").updInterval(100).build();
        var brk2 = Broker.builder().brkId(2).brkName("Kucoin").updInterval(50).build();
        brokers = List.of(brk1, brk2);
        when(brokerCache.getBrokers()).thenReturn(List.of(brk1, brk2));
        when(brokerCache.getTickers(anyInt())).thenReturn(List.of("t1", "t2"));
    }


    @Test
    void getBrokers() {
        var result = brokerService.getBrokers();
        assertEquals(result.size(), brokers.size());
        assertTrue(result.containsAll(brokers) && brokers.containsAll(result));
    }

    @Test
    void getBrokerTickers() {
        var result = brokerService.getBrokerTickers(1);
        assertEquals(result.size(), 2);
        assertTrue(result.containsAll(List.of("t1", "t2")));

    }
}