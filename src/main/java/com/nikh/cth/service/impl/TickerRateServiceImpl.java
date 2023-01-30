package com.nikh.cth.service.impl;

import com.nikh.cth.bean.request.TickerRateIntervalRequest;
import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.bean.ticker.TickerRateIntervalData;
import com.nikh.cth.cache.BrokerCache;
import com.nikh.cth.cache.TickerRateCache;
import com.nikh.cth.dao.TickerHistoryDao;
import com.nikh.cth.service.TickerRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TickerRateServiceImpl implements TickerRateService {

    @Autowired
    TickerRateCache tickerRateCache;
    @Autowired
    TickerHistoryDao tickerHistoryDao;


    @Override
    public List<TickerRate> getLastTickerRates(Integer brkId) {
        //TODO: implement
        return null;
    }

    @Override
    public List<TickerRate> getTickerHistory(Integer brkId) {
        //TODO: implement
        return null;
    }

    @Override
    public List<TickerRateIntervalData> getIntervalData(TickerRateIntervalRequest request) {
        //TODO: implement
        return null;
    }
}
