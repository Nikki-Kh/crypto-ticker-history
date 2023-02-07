package com.nikh.cth.cache;

import com.nikh.cth.bean.broker.BrokerToTickerDBEntry;
import com.nikh.cth.dao.BrokerDao;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.nikh.cth.bean.broker.Broker;

import javax.annotation.PostConstruct;
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

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void initCache() {
        brokers = brokerDao.getBrokers();
        brokerTickers = loadBrokerTickers();
        if (brokerTickers.isEmpty()) {
            throw new RuntimeException("Failed to load cache");
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
