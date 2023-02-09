package com.nikh.cth.unit.cache;

import com.nikh.cth.bean.broker.Broker;
import com.nikh.cth.bean.broker.BrokerToTickerDBEntry;
import com.nikh.cth.cache.BrokerCache;
import com.nikh.cth.dao.BrokerDao;

import com.nikh.cth.error.ServerException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ContextConfiguration(classes = {BrokerCache.class, BrokerDao.class})
class BrokerCacheTest {

    @Autowired
    BrokerCache brokerCache;

    @MockBean
    BrokerDao brokerDao;

    List<Broker> brokers;

    @Test
    void cacheInitFailsWithException() {
        when(brokerDao.getBrokers()).thenReturn(Collections.emptyList());
        assertThrows(ServerException.class, () -> brokerCache.initCache());
    }


    @Nested
    @SpringBootTest
    @ContextConfiguration(classes = {BrokerCache.class, BrokerDao.class})
    class BrokerCacheAfterInitTest {

        @BeforeEach
        void init() {
            var brk1 = Broker.builder().brkId(1).brkName("Kraken").updInterval(100).build();
            var brk2 = Broker.builder().brkId(2).brkName("Kucoin").updInterval(50).build();
            brokers = List.of(brk1, brk2);

            var list = new ArrayList<BrokerToTickerDBEntry>();
            list.add(new BrokerToTickerDBEntry(1, "tr1 tr2"));
            list.add(new BrokerToTickerDBEntry(2, "tr1 tr2"));
            when(brokerDao.getBrokers()).thenReturn(brokers);
            when(brokerDao.getAllTickers()).thenReturn(list);

            brokerCache.initCache();
        }

        @Test
        void testGetBrokers() {
            var result = brokerCache.getBrokers();
            assertEquals(2, result.size());
            assertTrue(result.containsAll(brokers));
        }

        @Test
        void testGetTickers() {
            var result = brokerCache.getTickers(1);
            assertEquals(result.size(), 2);
            assertTrue(result.containsAll(List.of("tr1", "tr2")));

        }


    }
}