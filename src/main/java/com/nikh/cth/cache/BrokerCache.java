package com.nikh.cth.cache;

import com.nikh.cth.bean.broker.BrokerToTickerDBEntry;
import com.nikh.cth.dao.BrokerDao;
import com.nikh.cth.error.ServerException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.nikh.cth.bean.broker.Broker;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BrokerCache {

    @Autowired
    private BrokerDao brokerDao;

    private List<Broker> brokers;
    private Map<Integer, List<String>> brokerTickers;

    @EventListener(value = ApplicationReadyEvent.class,
            condition = "@environment.getActiveProfiles()[0] != 'test'")
    @Order(1)
    public void initCache() {
        if (!CollectionUtils.isEmpty(brokers) && !MapUtils.isEmpty(brokerTickers)) {
            return;
        }
        brokers = brokerDao.getBrokers();
        brokerTickers = loadBrokerTickers();
        if (brokerTickers.isEmpty()) {
            throw new ServerException("Failed to load cache");
        }
    }

    public List<Broker> getBrokers() {
        return brokers;
    }

    public List<String> getTickers(Integer brkId) {
        return  brokerTickers.getOrDefault(brkId, Collections.emptyList());
    }

    private Map<Integer, List<String>> loadBrokerTickers() {
        return brokerDao.getAllTickers().stream()
                .collect(Collectors.toMap(
                        BrokerToTickerDBEntry::getBrkId,
                        it -> List.of(it.getTickerArrayStr().split(" ")))
                );
    }

}
