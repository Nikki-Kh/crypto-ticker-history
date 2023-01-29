package com.nikh.cth.service.impl;

import com.nikh.cth.bean.broker.Broker;
import com.nikh.cth.cache.BrokerCache;
import com.nikh.cth.service.BrokerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrokerServiceImpl implements BrokerService {

    private final BrokerCache brokerCache;

    @Override
    public List<Broker> getBrokers() {
        return brokerCache.getBrokers();
    }

    @Override
    public List<String> getBrokerTickers(Integer brkId) {
        return brokerCache.getTickers(brkId);
    }
}
