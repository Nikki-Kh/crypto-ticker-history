package com.nikh.cth.cache;

import com.nikh.cth.bean.ticker.TickerRate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class TickerRateCache {

    ConcurrentHashMap<Integer, List<TickerRate>> tickerRateCache = new ConcurrentHashMap<>();

    public void updateTickerRates(Integer key, List<TickerRate> values) {
        tickerRateCache.put(key, values);
    }

     public List<TickerRate> getAllLatestRates() {
        return tickerRateCache.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<TickerRate> getLatestRatesByBroker(Integer brkId) {
        return tickerRateCache.getOrDefault(brkId, Collections.emptyList());
    }

}
